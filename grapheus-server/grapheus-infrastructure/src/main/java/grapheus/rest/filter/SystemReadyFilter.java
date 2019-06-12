/**
 * 
 */
package grapheus.rest.filter;

import java.io.IOException;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import grapheus.persistence.ReadyForServingRequestsLatch;

/**
 * Filter which blocks incoming requests waiting while system is ready.
 * 
 * @author black
 */
@Provider
@Priority(5)
public class SystemReadyFilter implements ContainerRequestFilter {
    @Inject
    private ReadyForServingRequestsLatch readinessLatch;
    
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        readinessLatch.waitUntilReady();
    }
}
