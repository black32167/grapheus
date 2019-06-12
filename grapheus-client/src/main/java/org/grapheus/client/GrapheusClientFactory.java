/**
 * 
 */
package org.grapheus.client;

import java.util.function.Supplier;

import org.grapheus.client.api.HealthcheckAPI;
import org.grapheus.client.api.TelemetryAPI;
import org.grapheus.client.api.UserCreationAPI;
import org.grapheus.client.http.GrapheusRestClient;
import org.grapheus.client.http.auth.GrapheusClientCredentials;

/**
 * @author black
 */
public class GrapheusClientFactory {
    
    @FunctionalInterface
    public interface RestClientSupplier {
        GrapheusRestClient getRestClient(Supplier<GrapheusClientCredentials> credentialsSupplier);
    }
    
    private final RestClientSupplier clientSupplier;
    
    public GrapheusClientFactory(String baseURL) {
        this((credentialsSupplier) -> new GrapheusRestClient(baseURL, credentialsSupplier));
    }
    
    public GrapheusClientFactory(RestClientSupplier clientSupplier) {
        this.clientSupplier = clientSupplier;
    }
    
    public UserCreationAPI userCreator() {
        return new UserCreationAPI(clientSupplier.getRestClient(null));
    }
    
    public UserClient forUser(Supplier<GrapheusClientCredentials> credentialsSupplier) {
        return new UserClient(clientSupplier.getRestClient(credentialsSupplier));
    }

    public TelemetryAPI telemetry() {
        return new TelemetryAPI(clientSupplier.getRestClient(null));
    }

    public HealthcheckAPI healthcheck() {
        return new HealthcheckAPI(clientSupplier.getRestClient(null));
    }

    public String getBackendURL() {
        return clientSupplier.getRestClient(null).getBaseURL();
    }

}
