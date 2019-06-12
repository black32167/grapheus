/**
 * 
 */
package grapheus.view.extract;

import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import grapheus.persistence.model.graph.PersistentVertex;
import grapheus.view.extract.extractor.ArtifactPropertiesExtractor;
import lombok.RequiredArgsConstructor;

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
            pArtifact.setSemanticFeatures(new ArrayList<>());
        }
        
        // Extracting semantic features
        Arrays.asList(
                artifactPropertiesExtractor).forEach(extractor -> {
                    extractor.extractFeature(pArtifact);
                });

    }

}
