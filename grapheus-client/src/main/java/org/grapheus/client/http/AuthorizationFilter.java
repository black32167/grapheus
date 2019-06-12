/**
 * 
 */
package org.grapheus.client.http;

import java.io.IOException;
import java.util.function.Supplier;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

import org.grapheus.client.http.auth.GrapheusClientCredentials;
import org.grapheus.common.BasicAuthUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author black
 *
 */
@RequiredArgsConstructor
@Slf4j
public class AuthorizationFilter implements ClientRequestFilter {
    private final Supplier<GrapheusClientCredentials> credentialsSupplier;

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        GrapheusClientCredentials credentials = credentialsSupplier != null ? credentialsSupplier.get() : null;
        log.trace("Adding credentials for user {} in the request", (credentials == null ? "null" : credentials.getUserName()));
        if (credentials != null && credentials.getUserName() != null) {
            requestContext.getHeaders().add("Authorization",
                    BasicAuthUtil.basicToken(credentials.getUserName(), credentials.getSecret()));
        }
    }

}
