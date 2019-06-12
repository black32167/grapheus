/**
 * 
 */
package grapheus.remote.auth;

/**
 * @author black
 *
 */
public interface OAuthUrlsProvider {
    String getAccessTokenUrl();
    String getAuthorizationUrl(String clientId);
    boolean isSutableFor(String dsType);  
}
