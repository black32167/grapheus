/**
 * 
 */
package grapheus.persistence.graph.generate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import grapheus.persistence.exception.GraphExistsException;
import grapheus.persistence.model.graph.PersistentVertex;
import grapheus.persistence.storage.graph.EdgeStorage;
import grapheus.persistence.storage.graph.ExternalCompositeId;
import grapheus.persistence.storage.graph.VertexStorage;
import grapheus.persistence.storage.graph.query.EdgesFinder;
import grapheus.persistence.storage.graph.query.VertexFinder;

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
    private final EmptyGraphGenerator emptyGraphGenerator;
    private final VertexFinder vertexFinder;
    private final EdgesFinder edgesFinder;
    
    private final ExecutorService executor = Executors.newFixedThreadPool(4, (r) -> new Thread(r, "GraphGenerator"));
    
    public void generate(String grapheusUserKey, @NonNull String sourceGraphName, String _newGraphName, @NonNull String sourceProperty) throws GraphExistsException {
        final String newGraphName = _newGraphName == null ? sourceGraphName+"_"+sourceProperty : _newGraphName;
        emptyGraphGenerator.createGraph(grapheusUserKey, newGraphName);
        long start = System.currentTimeMillis();
        
        Collection<Future<?>> futuresV = new ArrayList<>();
        vertexFinder.iterateAllVertices(sourceGraphName, v -> {
            Future<?> f = executor.submit(() -> {
                List<String> values = getFeatureValues(sourceProperty, v);
                for(String value: values) {
                    String targetArtifactId = ExternalCompositeId.from(value);
  
                    if(!vertexStorage.getById(newGraphName, targetArtifactId).isPresent()) {
                        log.info("Creating new vertex '{}'", targetArtifactId);
                        vertexStorage.createVertex(newGraphName, 
                                PersistentVertex.builder().//
                                id(targetArtifactId).//
                                id(value).//
                                title(value).//
                                description("").//
                                build());
                    }
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
        edgesFinder.iterateEdges(sourceGraphName, e -> {
            Future<?> f = executor.submit(() -> {
                String fromV = e.getFrom().replaceAll(".*/", "");
                String toV = e.getTo().replaceAll(".*/", "");
                Optional<PersistentVertex> maybeVFrom = vertexStorage.getById(sourceGraphName, fromV);
                Optional<PersistentVertex> maybeVTo = vertexStorage.getById(sourceGraphName, toV);
                if(maybeVFrom.isPresent() && maybeVTo.isPresent()) {
                    List<String> valuesFrom = getFeatureValues(sourceProperty, maybeVFrom.get());
                    List<String> valuesTo = getFeatureValues(sourceProperty, maybeVTo.get());
                    for(String valueFromTarget : valuesFrom) {
                        for(String valueToTarget : valuesTo) {
                            Optional<PersistentVertex> maybeVFromTarget = vertexStorage.getById(newGraphName, ExternalCompositeId.from(valueFromTarget));
                            Optional<PersistentVertex> maybeVToTarget = vertexStorage.getById(newGraphName, ExternalCompositeId.from(valueToTarget));
                            if(maybeVFromTarget.isPresent() && maybeVToTarget.isPresent()) {   
                                edgesStorage.connect(newGraphName,
                                        maybeVFromTarget.get().getId(),
                                        maybeVToTarget.get().getId());
                            }
                        }
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
        log.info("Graph '{}' is succesfully generated in {} ms.", newGraphName, end-start);
       
    }
    
    private List<String> getFeatureValues(String featureName, PersistentVertex v) {
        return v.getSemanticFeatures().stream().//
            filter(f->featureName.equals(f.getFeature())).//
            map(f->f.getValue()).collect(Collectors.toList());
    }

}
