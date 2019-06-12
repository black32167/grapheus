/**
 * 
 */
package grapheus.runtime;

import java.util.Collection;
import java.util.List;

import grapheus.persistence.model.graph.PersistentVertex;

/**
 * @author black
 *
 */
public interface InflightArtifactsRegistry {
    Collection<PersistentVertex> register(List<PersistentVertex> unprocessedArtifactCandidatesChunk);
    void unregister(PersistentVertex processed);
    Collection<String> inFlight();
}
