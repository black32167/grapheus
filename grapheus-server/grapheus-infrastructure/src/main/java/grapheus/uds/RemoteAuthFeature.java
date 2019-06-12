/**
 * 
 */
package grapheus.uds;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.HttpHeaders;

import org.grapheus.common.BasicAuthUtil;

import lombok.RequiredArgsConstructor;
import grapheus.persistence.model.common.creds.DSBasicCredentials;
import grapheus.persistence.model.common.creds.DSCredentials;
import grapheus.persistence.model.common.creds.DSOAuthCredentials;

/**
 * @author black
 *
 */
@RequiredArgsConstructor
public class RemoteAuthFeature implements Feature {
    private final Supplier<Optional<DSCredentials>> credentialsSupplier;
    
    /**
     * @author black
     *
     */
    public class RemoteAuthFilter implements ClientRequestFilter {
        
        @Override
        public void filter(ClientRequestContext requestContext) throws IOException {
            credentialsSupplier.get().ifPresent((userCredentials) -> {
                if(userCredentials instanceof DSBasicCredentials) {
                    requestContext.getHeaders().add(HttpHeaders.AUTHORIZATION, calculateBasicAuthentication((DSBasicCredentials) userCredentials));
                } else if(userCredentials instanceof DSOAuthCredentials) {
                    DSOAuthCredentials oauthCreds = (DSOAuthCredentials) userCredentials;
                    requestContext.getHeaders().add(HttpHeaders.AUTHORIZATION, calculateOAuthAuthentication(oauthCreds));
                }
            });
        }

        private String calculateBasicAuthentication(DSBasicCredentials credentials) {
            Objects.requireNonNull(credentials,"Basic credentials are null");
            return BasicAuthUtil.basicToken(credentials.getUserName(), credentials.getUserPassword());
        }
        private String calculateOAuthAuthentication(DSOAuthCredentials userCredentials) {
            Objects.requireNonNull(userCredentials.getAccessTokenInfo());
            return "Bearer " + userCredentials.getAccessTokenInfo().getAccess_token();
        }
   
    }

    

    @Override
    public boolean configure(FeatureContext context) {
        context.register(new RemoteAuthFilter());
        return true;
    }

}
