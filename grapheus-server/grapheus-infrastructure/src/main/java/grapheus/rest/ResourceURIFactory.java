/**
 * 
 */
package grapheus.rest;

import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;

import org.springframework.stereotype.Service;

import grapheus.server.common.BaseURIProvider;
import grapheus.server.config.RestBackendConfig;


/**
 * @author black
 */
@Service
public class ResourceURIFactory implements BaseURIProvider {
    
    private final String BASE_URI;
    
    @Inject
    public ResourceURIFactory(RestBackendConfig restBackendConfig) {
    	BASE_URI = restBackendConfig.getBaseURI();
    }
    
    @Override
    public URI getBaseURI() {
        return URI.create(BASE_URI);
    }
    
    public URI getURI(String resource, Object... values) {
        return UriBuilder.//
                fromUri(BASE_URI).//
                path(resource).//
                build(values);
    }

}
