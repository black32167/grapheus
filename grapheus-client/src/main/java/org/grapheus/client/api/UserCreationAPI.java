/**
 * 
 */
package org.grapheus.client.api;

import org.grapheus.client.http.GrapheusRestClient;
import org.grapheus.client.model.RUser;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author black
 */
@AllArgsConstructor
@Slf4j
public class UserCreationAPI {
    private static String RESOURCE_PATH = "user";
    
    private final GrapheusRestClient restClient;
    
    public void createUser(String name, byte[] password) {
        restClient.post(RESOURCE_PATH, RUser.builder().//
                name(name).//
                password(password).//
                build());
    }
    public boolean checkUser(String name, byte[] password) {
        try {
            restClient.post(RESOURCE_PATH + "/check", RUser.builder().//
                    name(name).//
                    password(password).//
                    build());
            return true;
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return false;
        }
    }
    
}
