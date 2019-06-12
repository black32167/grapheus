/**
 * 
 */
package grapheus.rest.filter;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import org.grapheus.common.BasicAuthUtil;

import grapheus.context.GrapheusRequestContextHolder;
import grapheus.security.RequestContext;

/**
 * @author black
 *
 */
@Provider
@Priority(10)
public class RequestContextFilter implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String authTokenBase64 = requestContext.getHeaders().getFirst("Authorization");
        String user = null;
        byte[] userKey = null;
        if(authTokenBase64 != null && authTokenBase64.startsWith(BasicAuthUtil.BASIC_PREFIX)) {
            String[] authPair = BasicAuthUtil.decodeAuthToken(authTokenBase64);
            
            user = authPair[0];
            userKey = authPair[1].getBytes();
        }
        
        GrapheusRequestContextHolder.setContext(RequestContext.builder().//
                userId(user).//
                userSecret(userKey).//
                requestUrl(requestContext.getUriInfo().getRequestUri().toString()).//
                build());
     
    }

}
