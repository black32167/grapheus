/**
 * 
 */
package org.grapheus.client.api;

import java.net.URI;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.UriBuilder;

import org.grapheus.client.http.GrapheusRestClient;
import org.grapheus.client.http.ServerErrorResponseException;
import org.grapheus.client.model.RHealthcheckResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author black
 */
@RequiredArgsConstructor
@Slf4j
public class HealthcheckAPI {
    private final static long  CHECK_INTERVAL_MS = 1000;
    private static String HEALTHCHECK_RESOURCE_PATH = "healthcheck";
    private final GrapheusRestClient restClient;

    /**
     * Checks is RefleXiuon server is ready.
     */
    public boolean isReady() {
        URI uri = UriBuilder.fromPath(HEALTHCHECK_RESOURCE_PATH).build();
        try {
            return restClient.get(uri, RHealthcheckResponse.class).isReady();
        } catch (ServerErrorResponseException | ProcessingException e) {
            log.debug("Healthcheck request failed", e);   
        } 
        return false;
    }
    
    /**
     * Waits till RefleXiuon server is ready.
     */
    public boolean waitTillReady(long maxMills) {
        long initialTimestamp = System.currentTimeMillis();
        long timeLeft = maxMills;
        boolean ready = false;
        while(!(ready = isReady()) && timeLeft > 0) {
            try {
                Thread.sleep(Math.min(CHECK_INTERVAL_MS, maxMills));
            } catch (InterruptedException e) {}
            timeLeft = maxMills - (System.currentTimeMillis()-initialTimestamp);
        }
        
        return ready;
    }
}
