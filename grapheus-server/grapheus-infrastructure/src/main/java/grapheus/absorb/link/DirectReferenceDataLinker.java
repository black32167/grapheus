/**
 * 
 */
package grapheus.absorb.link;

import grapheus.persistence.model.graph.PersistentEdge;
import grapheus.persistence.model.graph.PersistentVertex;
import grapheus.persistence.storage.graph.ExternalCompositeId;
import grapheus.persistence.storage.graph.GraphNameUtils;
import grapheus.view.SemanticFeature;
import grapheus.view.extract.features.SemanticFeatureType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author black
 *
 */
@Service
public class DirectReferenceDataLinker implements RealtimeDataLinker {
    @Override
    public Collection<PersistentEdge> link(String graphName, PersistentVertex v) {
        List<PersistentEdge> connections = new ArrayList<>();

        Map<String, SemanticFeature> features = v.getSemanticFeatures();
        if(features != null) {
            String vertexCollectionName = GraphNameUtils.verticesCollectionName(graphName);

            Optional.ofNullable(features.get(SemanticFeatureType.LOCAL_ID_REFERENCE))
                    .map(SemanticFeature::getValue)//
                    .map(ExternalCompositeId::from)
                    .map(globalTargetId -> buildEdge(vertexCollectionName, v.getId(), globalTargetId))
                    .ifPresent(connections::add);
        }
        
        return connections;
    }

    private PersistentEdge buildEdge(String graphName, String globalSourceId, String globalTargetId) {
        return PersistentEdge.create(graphName, globalSourceId, globalTargetId);
    }
}
