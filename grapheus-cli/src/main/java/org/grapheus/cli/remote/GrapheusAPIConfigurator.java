/**
 * 
 */
package org.grapheus.cli.remote;

import org.grapheus.client.GrapheusClientFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author black
 */
@Configuration
public class GrapheusAPIConfigurator {
    private final GrapheusClientFactory clientFactory;
    
    public GrapheusAPIConfigurator(@Value("${grapheus.server.baseURL}") String baseGrapheusURL) {
        clientFactory = new GrapheusClientFactory(baseGrapheusURL);
    }
    
    @Bean
    public GrapheusClientFactory grapheusClientFactory() {
        return clientFactory;
    }
   
}
