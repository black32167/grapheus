/**
 * 
 */
package grapheus.rest.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import grapheus.context.GrapheusRequestContextHolder;
import grapheus.persistence.exception.DocumentNotFoundException;
import grapheus.persistence.storage.user.GrapheusUserStorage;
import grapheus.security.RequestContext;
import grapheus.security.credentials.HashService;

/**
 * @author black
 *
 */
@Provider
@Priority(100)
@Slf4j
public class GrapheusAuthFilter implements ContainerRequestFilter {
    private final static boolean skipAuthFilter = Boolean.getBoolean("skip.auth.filter");
    
    @Value
    private static class Exclusion {
        String method;
        String path;
    }
    
    @Inject
    private GrapheusUserStorage grapheusUserStorage;
    
    @Inject
    private HashService hashService;
    
    private final static List<Exclusion> exclusions = Arrays.asList(
            new Exclusion(HttpMethod.POST, "user"),
            new Exclusion(HttpMethod.POST, "user/check"),
            new Exclusion(HttpMethod.GET, "telemetry"),
            new Exclusion(HttpMethod.GET, "healthcheck"),
            new Exclusion(HttpMethod.OPTIONS, ".*")/*,
            new Exclusion(HttpMethod.GET, ".*")*/
    );
    
    public GrapheusAuthFilter() {
        if(skipAuthFilter) {
            log.warn("------------------------------------------------");
            log.warn("----- Authentication filter is DISABLED! -------");
            log.warn("------------------------------------------------");
        }
    }
  
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if(skipAuthFilter) {
            return;
        }
        RequestContext ctx = GrapheusRequestContextHolder.getContext();
        
        String requestMethod = requestContext.getMethod();
        String requestPath = requestContext.getUriInfo().getPath();
        boolean excludedFromFiltering = exclusions.stream().//
                anyMatch((e) -> e.method.equals(requestMethod) && requestPath.matches(e.path));
        if(!excludedFromFiltering) {
            byte[] storedHash = null;
            try {
                storedHash = Optional.ofNullable(ctx.getUserId()).//
                    map(grapheusUserStorage::getCredentialsHash).//
                    orElse(null);
            } catch (DocumentNotFoundException e) {
                throw new WebApplicationException("Cannot find user '" + ctx.getUserId() + "'", Status.UNAUTHORIZED);
            }

            byte[] suppliedHash = Optional.ofNullable(hashService.hash(ctx.getUserSecret())).//
                    orElseThrow(() -> new WebApplicationException("Credentials was not supplied", Status.UNAUTHORIZED));
            if(!Arrays.equals(storedHash, suppliedHash)) {
                throw new WebApplicationException("Mismatched credentials", Status.UNAUTHORIZED);
            }
        }
        
    }


}
