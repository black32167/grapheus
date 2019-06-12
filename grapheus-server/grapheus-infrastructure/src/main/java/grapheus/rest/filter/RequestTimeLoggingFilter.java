/**
 * 
 */
package grapheus.rest.filter;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import lombok.extern.slf4j.Slf4j;

/**
 * @author black
 *
 */
@Provider
@Priority(10)
@Slf4j
public class RequestTimeLoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {
 
    public RequestTimeLoggingFilter() {
        
    }
    
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        long start = System.currentTimeMillis();
        requestContext.setProperty("start", start);
   
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {
        Long start = (Long) requestContext.getProperty("start");
        if(start != null) {
            long end = System.currentTimeMillis();
            log.info("Request took " + (end-start) + " ms.");
        }
    }


}
