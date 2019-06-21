/**
 * 
 */
package grapheus.view.extract.extractor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import grapheus.persistence.model.graph.PersistentVertex;
import grapheus.view.SemanticFeature;
import grapheus.view.extract.features.SemanticFeatureType;

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
        List<SemanticFeature> newFeatures = new ArrayList<>();
        Optional.ofNullable(buildingArtifact.getSemanticFeatures()).ifPresent(ops->{
            ops.stream().filter(o->OPT_REF.equals(o.getFeature())).forEach(o->{
                newFeatures.add(//
                        SemanticFeature.builder().//
                            feature(SemanticFeatureType.LOCAL_ID_REFERENCE).//
                            value(o.getValue()).//
                            build());
            });
        });
        String localId = buildingArtifact.getId();
        newFeatures.add(//
                SemanticFeature.builder().//
                    feature(SemanticFeatureType.LOCAL_ID_IDENTITY).//
                    value(localId).//
                    build());
        
        buildingArtifact.getSemanticFeatures().addAll(newFeatures);
    }
}
