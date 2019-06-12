/**
 * 
 */
package grapheus.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import grapheus.persistence.model.graph.PersistentVertex;

/**
 * @author black
 *
 */
@Service
public class NonclusterProcessingArtifactsRegistry implements InflightArtifactsRegistry {
    private final Set<String> inflightIds = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Override
    public Collection<PersistentVertex> register(List<PersistentVertex> unprocessedArtifactCandidatesChunk) {
        if(unprocessedArtifactCandidatesChunk.isEmpty()) {
            return Collections.emptyList();
        }
        List<PersistentVertex> registeredArtifacts = new ArrayList<>();
        for(PersistentVertex nextCandidate: unprocessedArtifactCandidatesChunk) {
            if(inflightIds.add(nextCandidate.getExternalCompositeId())) {
                registeredArtifacts.add(nextCandidate);
            }
        }
        return registeredArtifacts;
    }

    @Override
    public Collection<String> inFlight() {
        return Collections.unmodifiableSet(inflightIds);
    }

    @Override
    public void unregister(PersistentVertex processed) {
        inflightIds.remove(processed.getExternalCompositeId());
    }


}
