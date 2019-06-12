/**
 * 
 */
package grapheus.persistence.model;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.arangodb.entity.DocumentField;
import com.arangodb.entity.DocumentField.Type;

import grapheus.persistence.model.annotation.Entity;
import grapheus.persistence.model.annotation.Index;

/**
 * Auxiliary class simplifies data retrieving from the model class.
 * 
 * @author black
 */
public final class ModelMeta {
    private static ConcurrentMap<Class<?>, Method> keyMethodCache = new ConcurrentHashMap<>();
    

    /**
     * Retrieves collection name from the entity annotated by @Entity.
     * NOTE: does not inspect subclasses.
     */
    public static String getCollectionName(Class<?> entityClass) {
        Entity entityMeta = Objects.requireNonNull(entityClass.getAnnotation(Entity.class));
        return entityMeta.name() == null ? entityClass.getSimpleName().toLowerCase() : entityMeta.name();
    }
    

    /**
     * Retrieves indexes info from the entity annotated by @Index annotations.
     * NOTE: does not inspect subclasses.
     */
    public static List<Index> getIndexes(Class<?> entityClass) {
        return Arrays.asList(entityClass.getAnnotationsByType(Index.class));
    }
    
    private ModelMeta() {}

    public static String getDocumentId(Object documentEntity) {
        Class<?> entityClass = Objects.requireNonNull(documentEntity, "Entity is null").getClass();
        Method getter = keyMethodCache.computeIfAbsent(entityClass, c->{
            // Looking for the document key
            for(Field field: entityClass.getDeclaredFields()) {
                DocumentField documentFieldMeta = field.getAnnotation(DocumentField.class);
                if(documentFieldMeta != null && Type.KEY == documentFieldMeta.value()) {
                    String fName = field.getName();
                    try {
                        return entityClass.getMethod("get" + fName.substring(0, 1).toUpperCase() + fName.substring(1));
                    } catch (NoSuchMethodException e) {
                        throw new IllegalArgumentException("Cannot retrieve id from " + documentEntity, e);
                    }
                }
            }
            throw new IllegalArgumentException("Cannot find id field in " + documentEntity);
            
        });
       
        try {
            return Objects.requireNonNull(getter.invoke(documentEntity), "Entity Id is null").toString();
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Cannot find document " + documentEntity, e);
        }
        
    }
}
