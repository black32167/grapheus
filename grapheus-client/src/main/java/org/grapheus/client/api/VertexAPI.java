/**
 * 
 */
package org.grapheus.client.api;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.ws.rs.core.UriBuilder;

import org.grapheus.client.http.GrapheusRestClient;
import org.grapheus.client.model.RUser;
import org.grapheus.client.model.RUserList;
import org.grapheus.client.model.graph.RPropertiesContainer;
import org.grapheus.client.model.graph.VertexInfoType;
import org.grapheus.client.model.graph.VerticesSortCriteria;
import org.grapheus.client.model.graph.edge.EdgeDirection;
import org.grapheus.client.model.graph.edge.REdgesContainer;
import org.grapheus.client.model.graph.search.RSearchRequest;
import org.grapheus.client.model.graph.vertex.RVertex;
import org.grapheus.client.model.graph.vertex.RVertexInfo;
import org.grapheus.client.model.graph.vertex.RVertexInfosContainer;
import org.grapheus.client.model.graph.vertex.RVerticesContainer;

import lombok.RequiredArgsConstructor;

/**
 * @author black
 */
@RequiredArgsConstructor
public class VertexAPI {
    private static final int READ_CHUNK_SIZE = 100;
    private static final String PATH_INDIVIDUAL_GRAPH = "graph/{graphName}/vertex";
    private static final String PARAM_EDGES_DIRECTION = "direction";
    private static final String PARAM_DEPTH = "depth";

    private final GrapheusRestClient restClient;
    
    public RVerticesContainer findVertices(String graphName, ArtifactsFilter filter, VerticesSortCriteria... sortingCriteria) {
        UriBuilder uriBuilder = graphPath(graphName).path("search");//
        RSearchRequest searchRequest = RSearchRequest.builder()
                .title(filter.getTitle())
                .sinks(filter.getSinks())
                .minEdgesSpec(filter.getMinAdjacentEdgesFilter() != null ? filter.getMinAdjacentEdgesFilter().serialize() : null)
                .verticesIds(filter.getVerticesIds() != null ? new ArrayList<>(filter.getVerticesIds()) : null)
                .sortingCriteriaSpec(sortingCriteria != null ? VerticesSortCriteria.serializeSortingCriteria(sortingCriteria) : null)
                .build();
     

        return restClient.post(
                uriBuilder.build().toString(),
                searchRequest,
                RVerticesContainer.class);
    }

    public List<String> getVertexPropertiesNames(String graphName) {
        URI uri = graphPath(graphName).path("properties").build();//
        return restClient.get(uri, RPropertiesContainer.class).getProperties();
    }
    

    public REdgesContainer getNeighbors(String graphName, String artifactId, EdgeDirection edgesDirection, int depth) {
        URI uri = graphPath(graphName).path("{artifactId}").path("neighbors").
                queryParam(PARAM_EDGES_DIRECTION, edgesDirection.name()).//
                queryParam(PARAM_DEPTH, depth).//
                build(artifactId);//
        return restClient.get(
                uri,
                REdgesContainer.class);
    }

    public List<RUser> getOwners(String graphName, String artifactId) {
        URI uri = graphPath(graphName).path("{artifactId}").path("owners").build(artifactId);//
        return Optional.ofNullable(restClient.get(
                uri,
                RUserList.class).getUsers()).//
                orElse(Collections.emptyList());
    }
    
    public void addVertex(String graphName, RVertex vertices) {
        restClient.put(graphPath(graphName).build().toString(), vertices);
    }

    public void addVertexBatch(String graphName, Collection<RVertex> vertices) {
        restClient.put(
                graphPath(graphName).path("batch").build().toString(),
                RVerticesContainer.builder().artifacts(vertices).build());
    }

    public RVertex getVertex(String graphName, String artifactId) {
        URI uri = graphPath(graphName).path("{artifactId}").build(artifactId);//
        return restClient.get(uri, RVertex.class); 
    }

    public Collection<RVertex> loadArtifacts(String graphName, Collection<String> artifactIds) {
        URI uri = graphPath(graphName).path("multiple").build();//
        ArrayList<RVertex> loadedArtifacts = new ArrayList<>();
        for(int i = 0; i < artifactIds.size(); i += READ_CHUNK_SIZE) {
            loadedArtifacts.addAll(restClient.get(
                uri.toString(),
                RVerticesContainer.class,
                Collections.singletonMap("artifactsKeys", String.join(",",
                        new ArrayList<>(artifactIds).subList(i, Math.min(i+READ_CHUNK_SIZE, artifactIds.size()))))).//
            getArtifacts());
        }
        return loadedArtifacts;
    }

    public List<RVertexInfo> getVerticesInfo(String graphName, VertexInfoType vertexInfoType, Collection<String> verticesIds) {
        URI uri = graphPath(graphName).path("info").build();//
        ArrayList<RVertexInfo> loadedInfos = new ArrayList<>();
        Map<String, Object> queryParameters = new HashMap<>();
        queryParameters.put("typeSpec", vertexInfoType.name());
        for(int i = 0; i < verticesIds.size(); i += READ_CHUNK_SIZE) {//TODO: duplicate code, create 'chunker' utility class?
            queryParameters.put("ids", String.join(",",
                    new ArrayList<>(verticesIds).subList(i, Math.min(i+READ_CHUNK_SIZE, verticesIds.size()))));
            loadedInfos.addAll(restClient.get(
                uri.toString(),
                RVertexInfosContainer.class,
                queryParameters).//
            getInfos());
        };
        return loadedInfos;
    }

    public void updateVertex(String graphName,String artifactId, RVertex vertex) {
        URI uri = graphPath(graphName).path("{vertexId}").path("update").build(artifactId);
        restClient.put(uri, vertex);
    }

    public void delete(String graphName, String artifactId) {
        URI uri = graphPath(graphName).path("{vertexId}").build(artifactId);
        restClient.delete(uri);
    }

    
    private UriBuilder graphPath(String graphName) {
        return UriBuilder.fromUri(UriBuilder.fromPath(PATH_INDIVIDUAL_GRAPH).build(graphName));
    }

    public void deleteRogueVertices(String graphName) {
        URI uri = graphPath(graphName).path("/rogue").build();
        restClient.delete(uri);
        
    }


}
