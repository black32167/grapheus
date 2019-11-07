/**
 * 
 */
package grapheus.view.extract.extractor;

import grapheus.persistence.model.graph.PersistentVertex;
import grapheus.view.SemanticFeature;
import grapheus.view.extract.features.SemanticFeatureType;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Pumps over artifact properties to the persistent container.
 * 
 * @author black
 *
 */
@Service
public class ArtifactPropertiesExtractor implements SemanticFeatureExtractor {
    private final static String OPT_REF = "refId";
    
    @Override
    public void extractFeature(PersistentVertex buildingArtifact) {
        Map<String, SemanticFeature> newFeatures = new HashMap<>();
        Optional.ofNullable(buildingArtifact.getSemanticFeatures()).ifPresent(ops->{
            ops.values().stream().filter(o->OPT_REF.equals(o.getFeature())).forEach(o->{
                newFeatures.put(//
                        SemanticFeatureType.LOCAL_ID_REFERENCE,//
                        SemanticFeature.builder().//
                            feature(SemanticFeatureType.LOCAL_ID_REFERENCE).//
                            value(o.getValue()).//
                            build());
            });
        });
        String localId = buildingArtifact.getId();
        newFeatures.put(//
                SemanticFeatureType.LOCAL_ID_IDENTITY, //
                SemanticFeature.builder().//
                    feature(SemanticFeatureType.LOCAL_ID_IDENTITY).//
                    value(localId).//
                    build());
        
        buildingArtifact.getSemanticFeatures().putAll(newFeatures);
    }
}
