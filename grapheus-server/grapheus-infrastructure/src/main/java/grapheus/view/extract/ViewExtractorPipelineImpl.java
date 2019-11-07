/**
 * 
 */
package grapheus.view.extract;

import grapheus.persistence.model.graph.PersistentVertex;
import grapheus.view.extract.extractor.ArtifactPropertiesExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashMap;

/**
 * @author black
 * TODO: over-complicated
 */
@Service
@RequiredArgsConstructor(onConstructor=@__({@Inject}))
public class ViewExtractorPipelineImpl implements ViewExtractorPipeline {
    private final ArtifactPropertiesExtractor artifactPropertiesExtractor;
  //  private final UrlExtractor urlExtractor;
    
    public void extractExtraFeatures(PersistentVertex pArtifact) {
        
        if(pArtifact.getSemanticFeatures() == null) {
            pArtifact.setSemanticFeatures(new HashMap<>());
        }
        
        // Extracting semantic features
        artifactPropertiesExtractor.extractFeature(pArtifact);
    }
}
