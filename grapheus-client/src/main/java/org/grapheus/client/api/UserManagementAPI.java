/**
 * 
 */
package org.grapheus.client.api;

import static java.util.Collections.singletonMap;
import static javax.ws.rs.core.UriBuilder.fromPath;

import org.grapheus.client.http.GrapheusRestClient;
import org.grapheus.client.model.security.RUserCredentials;

import lombok.AllArgsConstructor;

/**
 * @author black
 */
@AllArgsConstructor
public class UserManagementAPI {
    private static String CREDENTIALS_PATH = "user/{userKey}/credentials";
    private static String USER_PATH = "user/{userKey}";
    
    private final GrapheusRestClient restClient;

    public void delete(String userKey) {
        restClient.delete(fromPath(USER_PATH).buildFromMap(singletonMap("userKey", userKey)).toString());
    }

    public void setPassword(String userKey, byte[] grapheusUserSecret) {
        restClient.put(
                fromPath(CREDENTIALS_PATH).buildFromMap(singletonMap("userKey", userKey)).toString(),
                RUserCredentials.builder().userSecret(grapheusUserSecret).build());
        
    }
    
}
