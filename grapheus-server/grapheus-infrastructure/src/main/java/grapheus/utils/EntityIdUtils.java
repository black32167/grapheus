/**
 * 
 */
package grapheus.utils;

import java.util.Collection;
import java.util.stream.Collectors;

import grapheus.persistence.storage.graph.GraphNameUtils;
import lombok.NonNull;

/**
 * @author black
 *
 */
public final class EntityIdUtils {
    public static String toKey(@NonNull String id) {
        return id.replaceAll(".*/", "");
    }
    public static String toId(String graphId, @NonNull String key) {
        String vertexCollectionName = GraphNameUtils.verticesCollectionName(graphId);
        return vertexCollectionName + "/" + key;
    }

    public static Collection<String> toIds(String vertexCollectionName, Collection<String> verticesKeys) {
        return verticesKeys.stream().map(key -> vertexCollectionName + "/" + key).collect(Collectors.toList());
    }
    private EntityIdUtils() {}
    
}
