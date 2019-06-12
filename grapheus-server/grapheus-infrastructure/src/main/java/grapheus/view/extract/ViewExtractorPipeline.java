/**
 * 
 */
package grapheus.view.extract;

import grapheus.persistence.model.graph.PersistentVertex;

/**
 * @author black
 *
 */
public interface ViewExtractorPipeline {
    void extractExtraFeatures(PersistentVertex pArtifact);
}
