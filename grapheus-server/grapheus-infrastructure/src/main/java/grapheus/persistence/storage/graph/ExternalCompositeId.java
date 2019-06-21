/**
 * 
 */
package grapheus.persistence.storage.graph;

import lombok.NonNull;
import grapheus.persistence.model.graph.PersistentVertex;

/**
 * @author black
 */
public final class ExternalCompositeId {
    public static String from(@NonNull String externalId) {
        externalId = externalId.replaceAll("[^a-zA-Z0-9-]", "_");
        return externalId;
    }

    public static String from(PersistentVertex vertex) {
        return from(vertex.getId());
    }
    
    private ExternalCompositeId() {}

    public static String extractKeyFromCompleteId(String completeId) {
        return completeId.substring(completeId.indexOf('/')+1);
    }

    public static String buildCompleteId(String collectionName, String key) {
        return collectionName + "/" + key;
    }
}
