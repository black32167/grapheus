/**
 * 
 */
package org.grapheus.client.api;

import org.grapheus.client.http.GrapheusRestClient;
import org.grapheus.client.model.telemetry.RTelemetryContainer;

import lombok.AllArgsConstructor;

/**
 * @author black
 *
 */
@AllArgsConstructor
public class TelemetryAPI {
    private static String RESOURCE_PATH = "telemetry";
    
    private final GrapheusRestClient restClient;
    
    public RTelemetryContainer getTelemetry() {//TODO: on server-side: maintain cluster-wide configs of all telemetries
        return restClient.get(RESOURCE_PATH, RTelemetryContainer.class);
    }
}
