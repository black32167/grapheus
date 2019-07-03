/**
 * 
 */
package grapheus.rest.converter;

import org.grapheus.client.model.graph.edge.REdge;

import grapheus.persistence.model.graph.PersistentEdge;
import grapheus.persistence.storage.graph.ExternalCompositeId;
import grapheus.persistence.storage.traverse.Edge;
import org.grapheus.client.model.graph.vertex.RVertex;

import java.util.Collections;
import java.util.Optional;

import static java.util.Collections.*;
import static java.util.Optional.*;

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
                .tags(ofNullable(persistentEdge.getTags()).orElse(emptyList()))
                .build();
    }

    public static PersistentEdge toInternal(String verticesCollection, REdge e) {
        return PersistentEdge.builder()//
                .from(ExternalCompositeId.buildCompleteId(verticesCollection, e.getFrom()))
                .to(ExternalCompositeId.buildCompleteId(verticesCollection , e.getTo()))
                .tags(ofNullable(e.getTags()).orElse(emptyList()))
                .build();
    }

    public static PersistentEdge toInternal(String verticesCollection, String vertextId, RVertex.RReference reference) {
        String vertexId = ExternalCompositeId.buildCompleteId(verticesCollection, vertextId);
        String referenceId = ExternalCompositeId.buildCompleteId(verticesCollection , reference.getDestinationId());

        String sourceId = reference.isReversed() ? referenceId : vertexId;
        String destinationId = reference.isReversed() ? vertexId : referenceId;

        return PersistentEdge.builder()//
                .from(sourceId)
                .to(destinationId)
                .tags(ofNullable(reference.getTags()).orElse(emptyList()))
                .build();
    }
}
