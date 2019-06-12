/**
 * 
 */
package org.grapheus.client.api;

import java.util.Collections;
import java.util.List;

import org.grapheus.client.http.GrapheusRestClient;
import org.grapheus.client.model.graph.edge.REdge;
import org.grapheus.client.model.graph.edge.REdgesContainer;
import org.grapheus.client.model.graph.vertex.RVerticesIdsContainer;

import lombok.AllArgsConstructor;

/**
 * @author black
 *
 */
@AllArgsConstructor
public class ComputeAPI {
    private static String BASE_RESOURCE_PATH = "compute";
    private static String BRIDGES_RESOURCE_PATH = BASE_RESOURCE_PATH+"/bridges";
    private static String SINKS_RESOURCE_PATH = BASE_RESOURCE_PATH+"/sinks";
    private static String OUTBOUNDS_RESOURCE_PATH = BASE_RESOURCE_PATH+"/outbound";
    
    private final GrapheusRestClient restClient;
    
    public List<REdge> getBridges() {
        return restClient.get(BRIDGES_RESOURCE_PATH, REdgesContainer.class).getEdges();
    }

    public List<String> getSinks() {
        return restClient.get(SINKS_RESOURCE_PATH, RVerticesIdsContainer.class).getVertices();
    }
    
    public List<REdge> getOutbound(String rootVIds) {
        return restClient.get(OUTBOUNDS_RESOURCE_PATH, REdgesContainer.class, Collections.singletonMap("rootVerticesIds", rootVIds)).getEdges();
    }
}
