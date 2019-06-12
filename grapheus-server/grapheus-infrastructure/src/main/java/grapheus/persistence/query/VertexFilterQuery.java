/**
 * 
 */
package grapheus.persistence.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.grapheus.client.model.graph.SortDirection;
import org.grapheus.client.model.graph.edge.EdgeDirection;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import grapheus.persistence.storage.graph.GraphNameUtils;

/**
 * @author black
 */
@RequiredArgsConstructor
public class VertexFilterQuery {
    private final String graphName;

    private final Map<String, Object> parameters = new HashMap<>();
    private final List<String> vertexClauses = new ArrayList<>();
    private final List<String> sortCriteria = new ArrayList<>();
   
    private Map<EdgeDirection, EdgesFilterQuery> edgesFilters = new HashMap<>();
    
    private int start;
    private int limit = 100;

    private boolean count;

    public EdgesFilterQuery withEdgesSubquery(EdgeDirection direction) {
        return edgesFilters.computeIfAbsent(
                direction,
                (d) -> new EdgesFilterQuery(graphName, "a._id", "edges_" + d, parameters, sortCriteria, vertexClauses,  d));
    }
    
    public void addInFilter(String propertyName, Collection<String> propertyValues) {
        if (propertyValues != null) {
            String artsKeysArrPara = QueryUtil.arrayParameter("p" + vertexClauses.size() + "_", propertyValues, parameters);
            vertexClauses.add(" a." + propertyName + " IN " + artsKeysArrPara);
        }
    }

    public void addLikeFilter(String propertyName, String value) {
        if (value != null) {
            String paramName = "p" + vertexClauses.size();
            parameters.put(paramName, "%" + value + "%");
            vertexClauses.add(" a." + propertyName + " LIKE @" + paramName);
        }
    }

    public void sortByProperty(String property, @NonNull SortDirection sortDirection) {
        sortCriteria.add("a." + property + " " + sortDirection.name());
    }

    public String renderAQL() {
        String vertexCollection = GraphNameUtils.verticesCollectionName(graphName);

        StringBuilder artsFilter = new StringBuilder("FOR a IN "). //
                append(vertexCollection);

        edgesFilters.forEach((direction, edgesFilter) -> {
            artsFilter.append(" LET edges_" + direction + " = (" + edgesFilter.renderAQL() + ") ");
        });
      

        String filterFullVertexClause = vertexClauses.isEmpty() 
                ? ""
                : " FILTER " + vertexClauses.stream().collect(Collectors.joining(" AND "));

        artsFilter.append(filterFullVertexClause);
        
        if(!sortCriteria.isEmpty()) {
            artsFilter.append(" SORT ");
            artsFilter.append(sortCriteria.stream().collect(Collectors.joining(",")));
        }
        
        if(limit >= 0) {
            String limitClause = " LIMIT " + start + ", " + limit;
            artsFilter.append(limitClause);
        }
        
        if(count) {
            artsFilter.append(" COLLECT WITH COUNT INTO length ");
            artsFilter.append(" RETURN length ");
        } else {
            artsFilter.append(" RETURN a ");
        }
        return artsFilter.toString();
    }

    public Map<String, Object> parameters() {
        return Collections.unmodifiableMap(parameters);
    }

    public void setLimit(int start, int limit) {
        this.start = start;
        this.limit = limit;
        
    }

    public VertexFilterQuery count() {
        this.count = true;
        return this;
    }
}
