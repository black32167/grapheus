/**
 * 
 */
package grapheus.rest.converter;

import org.grapheus.client.model.graph.edge.REdge;

import grapheus.persistence.model.graph.PersistentEdge;
import grapheus.persistence.storage.graph.ExternalCompositeId;
import grapheus.persistence.storage.traverse.Edge;

/**
 * @author black
 */
public class EdgeConverter {
    public static REdge toREdge(Edge foundInternalEdge) {
        return REdge.builder()//
                .from(ExternalCompositeId.extractKeyFromCompleteId(foundInternalEdge.getFrom()))//
                .to(ExternalCompositeId.extractKeyFromCompleteId(foundInternalEdge.getTo()))//
                .build();
    }
    
    public static REdge toExternalEdge(PersistentEdge persistentEdge) {
        return REdge.builder()//
                .from(ExternalCompositeId.extractKeyFromCompleteId(persistentEdge.getFrom()))//
                .to(ExternalCompositeId.extractKeyFromCompleteId(persistentEdge.getTo()))//
                .build();
    }

    public static PersistentEdge toInternal(String verticesCollection, REdge e) {
        return PersistentEdge.builder()//
                .from(ExternalCompositeId.buildCompleteId(verticesCollection, e.getFrom()))
                .to(ExternalCompositeId.buildCompleteId(verticesCollection , e.getTo()))
                .build();
    }
           
}
