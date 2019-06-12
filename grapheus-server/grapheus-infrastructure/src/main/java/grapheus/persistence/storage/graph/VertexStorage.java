/**
 * 
 */
package grapheus.persistence.storage.graph;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import grapheus.persistence.model.graph.PersistentVertex;

/**
 * @author black
 *
 */
public interface VertexStorage {
    
    /**
     * Loads artifacts by their identifiers
     */
    List<PersistentVertex> get(String graphName, Collection<String> artifactIds);
    Optional<PersistentVertex> getByExternalId(String graphName, String externalCompositeId);
    
    void partiallyUpdateVertex(String graphName, PersistentVertex persistingArtifact);
    void updateVertex(String graphName, PersistentVertex persistingArtifact);
    void updateVertices(String graphName, Collection<PersistentVertex> vertices);
    void createVertex(String graphName, PersistentVertex persistingArtifact);
    void deleteVertex(String graphName, String artifactId);
    
    
    List<PersistentVertex> pickNextUnprocessedArtifacts(String graphName, Collection<String> artifactsInFlight);
    
    int getVerticesCount(String graphName);

    List<String> getAllArtifactsProperties(String graphName);

    void deleteGraph(String graphName);
    void deleteRogue(String graphName);
    void deleteVertices(String graphId, Collection<String> verticesIds);
    Iterable<PersistentVertex> getAllVertices(String graphName);
    
}
