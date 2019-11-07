/**
 * 
 */
package grapheus.persistence.graph.generate;

import grapheus.graph.GraphsManager;
import grapheus.persistence.exception.GraphExistsException;
import grapheus.persistence.model.graph.PersistentVertex;
import grapheus.persistence.storage.graph.EdgeStorage;
import grapheus.persistence.storage.graph.ExternalCompositeId;
import grapheus.persistence.storage.graph.VertexStorage;
import grapheus.persistence.storage.graph.query.EdgesFinder;
import grapheus.persistence.storage.graph.query.VertexFinder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Generates subgraph from specified one considering only vertices with the specified feature.
 * 
 * @author black
 */
@Service
@RequiredArgsConstructor(onConstructor = @__({ @Inject }))
@Slf4j
public class FeatureSubgraphGraphGenerator {
    private final VertexStorage vertexStorage;
    private final EdgeStorage edgesStorage;
    private final GraphsManager graphsManager;
    private final VertexFinder vertexFinder;
    private final EdgesFinder edgesFinder;
    
    private final ExecutorService executor = Executors.newFixedThreadPool(4, (r) -> new Thread(r, "GraphGenerator"));
    
    public void generate(String grapheusUserKey, @NonNull String sourceGraphId, String _newGraphName, @NonNull String sourceProperty) throws GraphExistsException {
        final String newGraphId = _newGraphName == null ? sourceGraphId+"_"+sourceProperty : _newGraphName;
        graphsManager.createGraphForUser(grapheusUserKey, newGraphId, sourceGraphId);
        long start = System.currentTimeMillis();
        
        Collection<Future<?>> futuresV = new ArrayList<>();
        vertexFinder.iterateAllVertices(sourceGraphId, v -> {
            Future<?> f = executor.submit(() -> {
                String value = getFeatureValue(sourceProperty, v);

                String targetArtifactId = ExternalCompositeId.from(value);
                if(!vertexStorage.getById(newGraphId, targetArtifactId).isPresent()) {
                    log.info("Creating new vertex '{}'", targetArtifactId);
                    vertexStorage.createVertex(newGraphId,
                            PersistentVertex.builder().//
                            id(targetArtifactId).//
                            id(value).//
                            title(value).//
                            description("").//
                            build());
                }
            });
            futuresV.add(f);
        });
        for(Future<?> f:futuresV) {
            try {
                f.get();
            } catch (Exception e) {
                log.error("", e);
            }
        }
        
        Collection<Future<?>> futuresE = new ArrayList<>();
        edgesFinder.iterateEdges(sourceGraphId, e -> {
            Future<?> f = executor.submit(() -> {
                String fromV = e.getFrom().replaceAll(".*/", "");
                String toV = e.getTo().replaceAll(".*/", "");
                Optional<PersistentVertex> maybeVFrom = vertexStorage.getById(sourceGraphId, fromV);
                Optional<PersistentVertex> maybeVTo = vertexStorage.getById(sourceGraphId, toV);
                if(maybeVFrom.isPresent() && maybeVTo.isPresent()) {
                    String valueFrom = getFeatureValue(sourceProperty, maybeVFrom.get());
                    String valueTo = getFeatureValue(sourceProperty, maybeVTo.get());
                    Optional<PersistentVertex> maybeVFromTarget = vertexStorage.getById(newGraphId, ExternalCompositeId.from(valueFrom));
                    Optional<PersistentVertex> maybeVToTarget = vertexStorage.getById(newGraphId, ExternalCompositeId.from(valueTo));
                    if(maybeVFromTarget.isPresent() && maybeVToTarget.isPresent()) {
                        edgesStorage.connect(newGraphId,
                                maybeVFromTarget.get().getId(),
                                maybeVToTarget.get().getId());
                    }
                }
            });
            futuresE.add(f);
        });
        for(Future<?> f:futuresE) {
            try {
                f.get();
            } catch (Exception e) {
                log.error("", e);
            }
        }
        long end = System.currentTimeMillis();
        log.info("Graph '{}' is succesfully generated in {} ms.", newGraphId, end-start);
       
    }
    
    private String getFeatureValue(String featureName, PersistentVertex v) {
        return v.getSemanticFeatures().get(featureName).getValue();
    }
}
