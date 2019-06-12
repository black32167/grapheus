/**
 * 
 */
package org.grapheus.client.api;

import java.io.InputStream;
import java.net.URI;
import java.util.List;

import javax.ws.rs.core.UriBuilder;

import org.grapheus.client.http.GrapheusRestClient;
import org.grapheus.client.http.ServerErrorResponseException;
import org.grapheus.client.model.RGraphInfo;
import org.grapheus.client.model.graph.RGraph;
import org.grapheus.client.model.graph.RGraphsContainer;
import org.grapheus.client.model.graph.VerticesSortCriteriaType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author black
 */
@RequiredArgsConstructor
@Slf4j
public class GraphAPI {
    private static final String PATH_GRAPH = "graph";

    private static final int HTTP_NOT_FOUND = 404;
    
    private final GrapheusRestClient restClient;
    
    public List<RGraph> getAvailableGraphs() {
        UriBuilder uriBuilder = UriBuilder.fromPath(PATH_GRAPH);//.path("all");
        return restClient.get(
                uriBuilder.build().//
                toString(),
                RGraphsContainer.class).getGraphs();
    }
    
    public void delete(String graphName) {
        URI uri = UriBuilder.fromPath(PATH_GRAPH).path(graphName).build();
        restClient.delete(uri);
    }

    public List<VerticesSortCriteriaType> getAvailableSortingCriteria(String graphId) {
        URI uri = UriBuilder.fromPath(PATH_GRAPH).path(graphId).build();
        return restClient.get(uri, RGraphInfo.class).getAvailableSortCriteria();
    }

    public boolean graphExists(String graphId) {
        URI uri = UriBuilder.fromPath(PATH_GRAPH).path(graphId).build();
        try {
            restClient.get(uri, RGraphInfo.class);
        } catch (ServerErrorResponseException e) {
            if(e.getCode() == HTTP_NOT_FOUND) {
                return false;
            } else {
                throw e;
            }
           
        }
        return true;
    }

    public InputStream export(String graphId) {
        URI uri = UriBuilder.fromPath(PATH_GRAPH).path(graphId).path("export").build();
        InputStream responseStream = restClient.get(uri, InputStream.class);
        return responseStream;
    }

    public void upload(String newGraphId, InputStream inputStream) {
        URI uri = UriBuilder.fromPath(PATH_GRAPH).path(newGraphId).path("import").build();
        restClient.post(uri, inputStream, "application/zip");
    }



}
