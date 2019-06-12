package grapheus.view.extract.extractor;

import grapheus.persistence.model.graph.PersistentVertex;

/**
 * !!!!!!!!!!!!! NOTE: instances of this interface are NOT supposed to be THREAD-SAFE !!!!!!!!!!!!!
 * 
 * Interface for all view extractors
 * @author black
 */
public interface SemanticFeatureExtractor {
    void extractFeature(PersistentVertex buildingArtifact);
}
