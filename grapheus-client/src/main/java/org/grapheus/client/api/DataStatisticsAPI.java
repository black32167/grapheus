/**
 * 
 */
package org.grapheus.client.api;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.grapheus.client.http.GrapheusRestClient;
import org.grapheus.client.model.RDataStatisticsContainer;

import lombok.RequiredArgsConstructor;

/**
 * @author black
 *
 */
@RequiredArgsConstructor
public class DataStatisticsAPI {
    private static String DSTAT_RESOURCE_PATH = "dstat";
    private final GrapheusRestClient restClient;
    
    public RDataStatisticsContainer getDataStat() {
        URI uri = UriBuilder.fromPath(DSTAT_RESOURCE_PATH).build();
        return restClient.get(uri, RDataStatisticsContainer.class);
    }
}
