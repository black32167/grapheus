/**
 * 
 */
package grapheus.persistence.query;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.arangodb.ArangoCursor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import grapheus.persistence.conpool.DBConnectionPool;
import grapheus.persistence.model.ModelMeta;

/**
 * @author black
 *
 */
@Slf4j
@RequiredArgsConstructor
public class AQLBuilderFactory {
    
    private final DBConnectionPool arangoDriverProvider;
   
    
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FilterItem {
        String name;
        Object value;
        String op;
    }


    public <T>AQLBuilder<T> from(Class<T> entityClass) {
        return new AQLBuilder<>(entityClass);
    }  
    
    public class AQLBuilder<T> {
        
        private String collection;
        private List<FilterItem> filter = new ArrayList<>();
        private boolean filtersOrConcatenator;
        private String action = "RETURN";
        private boolean distinct;
        
        private List<String> ascSortFields = new ArrayList<>();
        private List<String> descSortFields = new ArrayList<>();

        private Integer limit;
        private Class<T> defaultEntityClass;
        boolean flatten;
        
        public AQLBuilder(Class<T> entityClass) {
            this.defaultEntityClass = entityClass;
            collection = ModelMeta.getCollectionName(entityClass);
            
        }
        public AQLBuilder<T> flatten() {
            this.flatten = true;
            return this;
        }
        public AQLBuilder<T> or() {
            filtersOrConcatenator = true;
            return this;
        }
        public AQLBuilder<T> asc(String field) {
            this.ascSortFields.add(field);
            return this;
        }    
    
        public AQLBuilder<T> desc(String field) {
            this.descSortFields.add(field);
            return this;
        }  
        public AQLBuilder<T> distinct(boolean distinct) {
            this.distinct = distinct;
            return this;
        }    
    
        public AQLBuilder<T> limit(int limit) {
            this.limit = limit;
            return this;
        }

        public AQLBuilder<T> filter(String field, String op, Object value) {
            filter.add(FilterItem.builder().//
                    name(field).//
                    op(op).//
                    value(value).//
                    build());
            return this;
        }

        public AQLBuilder<T> filter(String field,  Object value) {
            return this.filter(field, "==", value);
        }
        
        public AQLBuilder<T> filter(String clause) {
            return this.filter(clause, null, null);
        }
    
        public void remove() {
            this.action = format("REMOVE i in %s", collection);
            build(Void.class);
        }
    
        public <C> ArangoCursor<C> ret(Class<C> type, String projection) {
            this.action = returnPrefix() + format(" i.%s", projection);
            return build(type);
        }
    
        public ArangoCursor<T> ret(Class<T> type, List<String> projections) {
            this.action = returnPrefix() +  " " +
                    projections.stream().map((p) -> p + ":i." + p).collect(Collectors.joining(",", "{", "}"));
            return build(type);
        }
        public ArangoCursor<T> ret(Class<T> type) {
            Objects.requireNonNull(type, "The entity type is null");
            this.action = "RETURN i";
            return build(type);
        }
        
        public ArangoCursor<T> ret() {
            return (ArangoCursor<T>) ret(defaultEntityClass);
        }
    
    
        private String returnPrefix() {
            return "RETURN" + (distinct ? " DISTINCT" : "");
        }
        
        private String sortClause() {
            List<String> sb = new ArrayList<>();
            if(!ascSortFields.isEmpty()) {
                sb.add("SORT");
                sb.add(String.join(",", ascSortFields.stream().map((f) -> "i." + f).collect(Collectors.toList())));
                sb.add("ASC");
            }
            if(!descSortFields.isEmpty()) {
                sb.add("SORT");
                sb.add(String.join(",", descSortFields.stream().map((f) -> "i." + f).collect(Collectors.toList())));
                sb.add("DESC");
            }
            return String.join(" ", sb);
        }
    
        private <R> ArangoCursor<R> build(Class<R> type) {
            List<String> sb = new ArrayList<>();
            
            sb.add(format("FOR i in %s", collection));
            
            HashMap<String, Object> bindVars = new HashMap<>();
            if(!filter.isEmpty()) {
                sb.add("FILTER");
                List<String> filterChunks = new ArrayList<>();
                int i = 0;
                for(FilterItem f: filter) {
                    if(f.getValue() == null) {
                        if(f.getOp() == null) {
                            filterChunks.add(format("i.%s", f.getName()));
                        } else {
                            filterChunks.add(format("i.%s %s null", f.getName(), f.getOp()));
                        }
                    } else if("VAL_IN".equals(f.getOp())) {
                        String parAlias = format("p%s", i++);
                        String val = "@" + parAlias;
                        bindVars.put(parAlias, f.getName());
                        filterChunks.add(format("%s %s i.%s", val, "IN", f.getValue()));
                    } else {
                        String val;
                        if(f.getValue() instanceof Collection) {
                            val = "[";
                            
                            Collection<String> aliases = new ArrayList<>();
                            for(Object element: ((Collection<?>)f.getValue())) {
                                String parAlias = format("p%s", i++);
                                aliases.add(parAlias);
                                bindVars.put(parAlias, element);
                            }
                            val += String.join(",", aliases.stream().map(s -> "@" + s).collect(Collectors.toList()));
                            
                            val += "]";
                        } else {
                            String parAlias = format("p%s", i++);
                            val = "@" + parAlias;
                            bindVars.put(parAlias, f.getValue());
                        }

                        filterChunks.add(format("i.%s %s %s", f.getName(), f.getOp(), val));
                    }
                }
                sb.add(String.join(filtersOrConcatenator ? " || " : " && ", filterChunks));
            }
            
            sb.add(sortClause());
            
            if(limit != null) {
                sb.add(format("LIMIT %s", limit));
            }
            
            sb.add(action);
            
            return arangoDriverProvider.query((db) -> {
                String aql = String.join(" ", sb);
                if(flatten) {
                    aql = "RETURN (" + aql + ")[**]";
                }
                log.debug("Running AQL:{}", aql);
                
                return db.query(aql, bindVars, null, type);
            });
        }
    
        public long count() {
            this.action = "COLLECT WITH COUNT into length RETURN length";
            return build(Long.class).next();
        }
        
        public <R> R aggregate(String function, String field, Class<R> resultClass) {
            this.action = format("COLLECT AGGREGATE agg = %s(i.%s) RETURN agg", function, field);
            return build(resultClass).next();
        }
    
        public List<T> list() {
            return this.ret().asListRemaining();
        }
    
        public Optional<T> first() {
            ArangoCursor<T> cursor = ret();
            
            return cursor.hasNext() ? Optional.of(cursor.next()) : Optional.empty();
        }
    }
}
