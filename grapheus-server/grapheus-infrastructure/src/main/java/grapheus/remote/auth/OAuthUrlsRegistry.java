/**
 * 
 */
package grapheus.remote.auth;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.NonNull;

/**
 * @author black
 *
 */
@Service
public class OAuthUrlsRegistry {

    @Autowired(required = false)
    private List<OAuthUrlsProvider> remoteTokenRetrievers;

    public Optional<String> getAuthorizationURLByClientId(@NonNull String sourceType, @NonNull String oauthClientId) {
        return findProvider(sourceType).map(p -> p.getAuthorizationUrl(oauthClientId));
    }
    public String getAccessTokenURL(@NonNull String sourceType) {
        return findProvider(sourceType).map(p -> p.getAccessTokenUrl()).//
                orElseThrow(() -> new IllegalArgumentException("Cannot find access token url for datasource of type " + sourceType));
    }
    private Optional<OAuthUrlsProvider> findProvider(String sourceType) {
        if(remoteTokenRetrievers == null) {
            return Optional.empty();
        }
        return remoteTokenRetrievers.stream().//
                filter(cr->cr.isSutableFor(sourceType)).//
                findAny();
    }
}
