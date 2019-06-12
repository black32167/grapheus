/**
 * 
 */
package org.grapheus.client.api;

import java.net.URI;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.UriBuilder;

import org.grapheus.client.http.GrapheusRestClient;
import org.grapheus.client.model.graph.edge.EdgeDirection;
import org.grapheus.client.model.graph.generate.RConnectionRequest;
import org.grapheus.client.model.graph.generate.RDisconnectionRequest;
import org.grapheus.client.model.graph.generate.RGraphCreationParameters;
import org.grapheus.client.model.graph.generate.RMergeRequest;
import org.grapheus.client.model.graph.generate.RPathGraphParameters;
import org.grapheus.client.model.graph.operation.RVerticesRemoveOperationContainer;
import org.grapheus.client.model.graph.operation.path.RPathContainer;
import org.grapheus.client.model.graph.operation.path.RShortestPathParameters;

import lombok.RequiredArgsConstructor;

/**
 * @author black
 *
 */
@RequiredArgsConstructor
public class OperationAPI {
    private static final String PATH_OPERATION = "operation";
    private static final String PARAM_SOURCE_GRAPH = "sourceGraphId";
    private final GrapheusRestClient restClient;
    
    public void generateGraphFromFeature(String sourceGraphName, String targetGraphName, String featureName) {
        URI uri = UriBuilder.fromPath(PATH_OPERATION).path("propertyGraph")
                .queryParam(PARAM_SOURCE_GRAPH, sourceGraphName)
                .build();
        restClient.post(uri, RGraphCreationParameters.propertyBased(targetGraphName, featureName));
    }
    
    public void generateCyclicGraph(String sourceGraphName, String targetGraphName) {
        URI uri = UriBuilder.fromPath(PATH_OPERATION).path("cyclicGraph")
                .queryParam(PARAM_SOURCE_GRAPH, sourceGraphName)
                .build();
        restClient.post(uri, RGraphCreationParameters.cyclic(targetGraphName));
    }
    
    public void generateEmptyGraph(String newGraphName) {
        URI uri = UriBuilder.fromPath(PATH_OPERATION).path("emptyGraph").build();
        restClient.post(uri, RGraphCreationParameters.empty(newGraphName));
    }

    public void generateCloneGraph(String sourceGraphName, String newGraphName) {
        URI uri = UriBuilder.fromPath(PATH_OPERATION).path("cloneGraph")
                .queryParam(PARAM_SOURCE_GRAPH, sourceGraphName)
                .build();
        restClient.post(uri, RGraphCreationParameters.clone(newGraphName));
    }
    
    public void generateSelfGraph(String newGraphName) {
        URI uri = UriBuilder.fromPath(PATH_OPERATION).path("selfGraph").build();
        restClient.post(uri, RGraphCreationParameters.self(newGraphName));
    }

    public void generateTraversalGraph(String sourceGraphName, String newGraphName, String startVertexId, EdgeDirection traversalDirection) {
        URI uri = UriBuilder.fromPath(PATH_OPERATION).path("traversalGraph")
                .queryParam(PARAM_SOURCE_GRAPH, sourceGraphName)
                .build();
        restClient.post(uri, RGraphCreationParameters.traverse(newGraphName, startVertexId, traversalDirection));
    }

    public void generateTopologicalOrder(String sourceGraphName) {
        URI uri = UriBuilder.fromPath(PATH_OPERATION).path("topologicalSort")
                .queryParam(PARAM_SOURCE_GRAPH, sourceGraphName)
                .build();
        restClient.post(uri, null);
    }

    public void merge(String graphId, String newVertexName, List<String> verticesIds) {
        URI uri = UriBuilder.fromPath(PATH_OPERATION).path("merge").build();
        restClient.post(uri, RMergeRequest.builder()
                .graphId(graphId)
                .verticesIds(verticesIds)
                .newVertexName(newVertexName)
                .build());
    }
    

    public void connect(String graphId, List<String> fromVertices, List<String> toVertices) {
        URI uri = UriBuilder.fromPath(PATH_OPERATION).path("connect").build();
        restClient.post(uri, RConnectionRequest.builder()
                .graphId(graphId)
                .fromVerticesIds(fromVertices)
                .toVerticesIds(toVertices)
                .build());
    }


    public void disconnect(String graphId, String fromVertexId, String toVertexId) {
        URI uri = UriBuilder.fromPath(PATH_OPERATION).path("disconnect").build();
        restClient.post(uri, RDisconnectionRequest.builder()
                .graphId(graphId)
                .fromVertexId(fromVertexId)
                .toVertexId(toVertexId)
                .build());
    }
    
    public void generatePathGraph(
            String sourceGraphName, String newGraphName,
            Collection<String> boundaryVerticesIds) {
        URI uri = UriBuilder.fromPath(PATH_OPERATION).path("pathGraph")
                .queryParam(PARAM_SOURCE_GRAPH, sourceGraphName)
                .build();
        restClient.post(uri, RPathGraphParameters.builder()
                .sourceGraphName(sourceGraphName)
                .newGraphName(newGraphName)
                .boundaryVerticesIds(boundaryVerticesIds));
    }

    public void deleteVertices(String graphId, Collection<String> verticesIds) {
        URI uri = UriBuilder.fromPath(PATH_OPERATION).path("vertices").path("delete").build();
        restClient.post(uri, RVerticesRemoveOperationContainer.builder()
                .graphId(graphId)
                .verticesIds(verticesIds)
                .build());
    }

    public List<String> shortestPath(String graphId, String fromVertexId, String toVertexId) {
        URI uri = UriBuilder.fromPath(PATH_OPERATION).path("vertices").path("shortestPath").build();
        RPathContainer pathContainer = restClient.post(uri, RShortestPathParameters.builder()
                .graphId(graphId)
                .fromVertexId(fromVertexId)
                .toVertexId(toVertexId)
                .build(),
                RPathContainer.class);
        return pathContainer.getPathVericesIds();
    }

}
