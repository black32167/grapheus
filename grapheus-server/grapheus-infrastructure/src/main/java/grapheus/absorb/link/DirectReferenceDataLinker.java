/**
 * 
 */
package grapheus.absorb.link;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import grapheus.persistence.model.graph.PersistentEdge;
import grapheus.persistence.model.graph.PersistentVertex;
import grapheus.persistence.storage.graph.ExternalCompositeId;
import grapheus.persistence.storage.graph.GraphNameUtils;
import grapheus.view.SemanticFeature;
import grapheus.view.extract.features.SemanticFeatureType;

/**
 * @author black
 *
 */
@Service
public class DirectReferenceDataLinker implements RealtimeDataLinker {
    @Override
    public Collection<PersistentEdge> link(String graphName, PersistentVertex v) {
        List<PersistentEdge> connections = new ArrayList<>();

        List<SemanticFeature> features = v.getSemanticFeatures();
        if(features != null) {
            String vertexCollectionName = GraphNameUtils.verticesCollectionName(graphName);

            features.stream().//
                    filter(f -> SemanticFeatureType.LOCAL_ID_REFERENCE.equals(f.getFeature())).//
                    map(f -> f.getValue()).//
                    map(localTargetId -> ExternalCompositeId.from(localTargetId)).
                    map(globalTargetId -> buildEdge(vertexCollectionName, v.getId(), globalTargetId)).
                    collect(Collectors.toCollection(() -> connections));
        }
        
        return connections;
    }

    private PersistentEdge buildEdge(String vertexCollectionName, String globalSourceId, String globalTargetId) {
        return PersistentEdge.builder().//
                from(vertexCollectionName + "/" + globalSourceId).//
                to(vertexCollectionName + "/" + globalTargetId).//
                build();
    }

}
