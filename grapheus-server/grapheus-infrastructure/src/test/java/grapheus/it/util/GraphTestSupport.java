/**
 * 
 */
package grapheus.it.util;

import grapheus.it.TestConstants;
import grapheus.persistence.exception.GraphExistsException;
import grapheus.persistence.foxx.GrapheusFoxxService;
import grapheus.persistence.model.graph.PersistentVertex;
import grapheus.persistence.storage.graph.EdgeStorage;
import grapheus.persistence.storage.graph.GraphStorage;
import grapheus.persistence.storage.graph.VertexStorage;
import grapheus.persistence.storage.graph.impl.DefaultEdgeStorage;
import grapheus.persistence.storage.graph.impl.DefaultGraphStorage;
import grapheus.persistence.storage.graph.impl.DefaultVertexStorage;
import grapheus.persistence.storage.graph.query.EdgesFinder;
import grapheus.persistence.storage.graph.query.VertexFinder;
import grapheus.persistence.storage.graph.query.impl.DefaultEdgesFinder;
import grapheus.persistence.storage.graph.query.impl.DefaultVertexFinder;
import grapheus.persistence.testutil.DbTestsContextConfig;
import grapheus.service.uds.ArtifactsFilter;
import grapheus.view.SemanticFeature;
import grapheus.view.extract.features.SemanticFeatureType;
import lombok.RequiredArgsConstructor;
import org.grapheus.client.model.graph.VerticesSortCriteria;
import org.grapheus.client.model.graph.edge.EdgeDirection;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author black
 *
 */
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes={
        DbTestsContextConfig.class,
        DefaultGraphStorage.class,
        DefaultVertexStorage.class,
        DefaultEdgeStorage.class,
        DefaultVertexFinder.class,
        DefaultEdgesFinder.class,
        GrapheusFoxxService.class
})
@TestPropertySource(TestConstants.DB_PROPERTIES)
abstract public class GraphTestSupport {
    
    @RequiredArgsConstructor
    protected static class EdgeDefinition {
        private final String fromKey;
        private final String toKey;    
    }

    @RequiredArgsConstructor
    protected static class Property {
        private final String name;
        private final String value;
    }

    @RequiredArgsConstructor
    protected static class VertexDefinition {
        private final String key;
        private final List<SemanticFeature> properties = new ArrayList<>();
    }
    
    @RequiredArgsConstructor
    protected class GraphBuilder {
        private final String graphName;
        
        private final List<EdgeDefinition> edges = new ArrayList<>();
        private final Map<String, VertexDefinition> verticesDefinition = new HashMap<>();
        private final List<PersistentVertex> directVertices = new ArrayList<PersistentVertex>();
        
        public GraphBuilder connect(String fromKey, String toKey) {
            if(!verticesDefinition.containsKey(fromKey)) verticesDefinition.put(fromKey, new VertexDefinition(fromKey));
            if(!verticesDefinition.containsKey(toKey)) verticesDefinition.put(toKey, new VertexDefinition(toKey));
            edges.add(new EdgeDefinition(fromKey, toKey));
            return this;
        }

        public GraphBuilder vertex(PersistentVertex vertex) {
            directVertices.add(vertex);
            return this;
        }

        public GraphBuilder prop(String vertexKey, String propertyName, String propertyValue) {
            verticesDefinition
                    .computeIfAbsent(vertexKey, VertexDefinition::new)
                    .properties.add(SemanticFeature.builder().feature(propertyName).value(propertyValue).build());
            return this;
        }

        public void build() throws GraphExistsException {
            graphStorage.addGraph(graphName);
            for(VertexDefinition v: verticesDefinition.values()) {
                v.properties.add(SemanticFeature.builder().//
                        value(v.key).
                        feature(SemanticFeatureType.LOCAL_ID_REFERENCE).
                        build());
                vertexStorage.createVertex(graphName, PersistentVertex.builder() //
                        .description("")
                        .title("")
                        .id(v.key)
                        .semanticFeatures(SemanticFeature.toMap(v.properties))
                        .build());
            }
            for(PersistentVertex v: directVertices) {
                vertexStorage.createVertex(graphName, v);
            }
            for(EdgeDefinition e: edges) {
                edgeStorage.connect(graphName, e.fromKey, e.toKey);
            }
        }
    }

    @Inject
    private VertexStorage vertexStorage;
    
    @Inject
    private EdgeStorage edgeStorage;
    
    @Inject
    private GraphStorage graphStorage;
    
    @Inject
    private VertexFinder vertexFinder;
    
    @Inject
    private EdgesFinder edgesFinder;
    
    protected GraphBuilder graph(String graphName) {
       return new GraphBuilder(graphName);
    }
    
    protected Collection<String> findVerticesKeys(String graphName, VerticesSortCriteria verticesSortCriteria) {
        return vertexFinder.findVerticesByCriteria(
                graphName,
                ArtifactsFilter.builder().limit(100).build(), 
                Collections.singletonList(verticesSortCriteria)).getVertices()
                    .stream()
                    .map(PersistentVertex::getId)
                    .collect(Collectors.toList());
    }

    protected Collection<String> findInboundConnections(String graphName, String vertexId) {
        return edgesFinder.getNeighbors(graphName, vertexId, EdgeDirection.INBOUND, 1).stream()
                .map(e->e.getFrom())
                .collect(Collectors.toList());
    }

    protected Collection<String> findOutboundConnections(String graphName, String vertexId) {
        return edgesFinder.getNeighbors(graphName, vertexId, EdgeDirection.OUTBOUND, 1).stream()
                .map(e->e.getTo())
                .collect(Collectors.toList());
    }

    protected PersistentVertex loadVertex(String graphName, String vertexId) {
        return vertexStorage.getById(graphName, vertexId).get();
    }

    protected void updateVertex(String graphName, PersistentVertex vertex) {
        vertexStorage.updateVertex(graphName, vertex);
    }
}
