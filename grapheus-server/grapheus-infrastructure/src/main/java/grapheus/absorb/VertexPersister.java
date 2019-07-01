/**
 * 
 */
package grapheus.absorb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import grapheus.absorb.link.RealtimeDataLinker;
import grapheus.persistence.model.graph.PersistentEdge;
import grapheus.persistence.model.graph.PersistentVertex;
import grapheus.persistence.storage.graph.EdgeStorage;
import grapheus.persistence.storage.graph.ExternalCompositeId;
import grapheus.persistence.storage.graph.GraphStorage;
import grapheus.persistence.storage.graph.VertexStorage;
import grapheus.utils.ListenerUtils;
import grapheus.view.extract.ViewExtractorPipeline;

/**
 * @author black
 */
@Service
@RequiredArgsConstructor(onConstructor=@__({@Inject}))
@Slf4j
public class VertexPersister {
    
    private final ViewExtractorPipeline viewExtractor; 
    private final VertexStorage vertexStorage;
    private final EdgeStorage edgeStorage;
    private final GraphStorage graphStorage;
    
    @Autowired(required = true)
    private List<RealtimeDataLinker> realtimeDataLinkers;
    
    /**
     * Returns true if artifact was updated.
     */
    public boolean update(String graphName, @NonNull PersistentVertex newVertex) {

        String artifactId = ExternalCompositeId.from(newVertex);
        newVertex.setId(artifactId);
        
        // Looking for existing artifact
        Optional<PersistentVertex> maybeOldVertex = vertexStorage.//
                getById(graphName, artifactId);
        
        // Extract features
        viewExtractor.extractExtraFeatures(newVertex);
        if(maybeOldVertex.isPresent()) {
            PersistentVertex oldVertex = maybeOldVertex.get();
            newVertex.setRev(oldVertex.getRev());

            if(oldVertex.getUpdatedTimestamp() != null && newVertex.getUpdatedTimestamp() != null &&  oldVertex.getUpdatedTimestamp() >= newVertex.getUpdatedTimestamp()) {
                log.debug("Artifact is already up-to-date:#{}", artifactId);
                return false; // Not updated
            }
            vertexStorage.updateVertex(graphName, newVertex);
            
        } else {
            vertexStorage.createVertex(graphName, newVertex);
        }

        ListenerUtils.iterateLogExceptions(realtimeDataLinkers, l-> link(graphName, l, newVertex));
        
        graphStorage.setUnprocessed(graphName);
        return true;
              
    }

    private void link(String graphName, RealtimeDataLinker l, PersistentVertex newVertex) {
        Collection<PersistentEdge> connections = l.link(graphName, newVertex);
        edgeStorage.bulkConnect(graphName, connections);
        
    }

    public void partialUpdate(String graphName, PersistentVertex updatePayload) {
        vertexStorage.partiallyUpdateVertex(graphName, updatePayload);
    }

    public void bulkUpdate(String graphName, Collection<PersistentVertex> vertices) {
        Collection<PersistentEdge> connections = new ArrayList<PersistentEdge>();
        vertices.forEach(v->{
            String artifactId = ExternalCompositeId.from(v);
            v.setId(artifactId);
            if(v.getDescription() == null || v.getDescription().trim().isEmpty()) {
                v.setDescription(v.getTitle());
            }
            
            viewExtractor.extractExtraFeatures(v);
            
            ListenerUtils.iterateLogExceptions(realtimeDataLinkers, l-> connections.addAll(l.link(graphName, v)));
        });
        vertices.addAll(generateEphemeralVertices(vertices, connections));
        vertexStorage.updateVertices(graphName, vertices);
        edgeStorage.bulkConnect(graphName, connections);
        
    }

    private Collection<PersistentVertex> generateEphemeralVertices(
            Collection<PersistentVertex> vertices,
            Collection<PersistentEdge> connections) {
        Collection<PersistentVertex> ephemeralVertices = new HashSet<PersistentVertex>();
        Set<String> existingVerticesIds = vertices.stream().map(v->v.getId()).collect(Collectors.toSet());
        connections.forEach(c->{
            String globalId = ExternalCompositeId.extractKeyFromCompleteId(c.getTo());
            if(!existingVerticesIds.contains(globalId)) {
                ephemeralVertices.add(PersistentVertex.builder()
                        .id(globalId)
                        //.title("?"+globalId)
                        .build());
            }
        });
        return ephemeralVertices;
    }



}
