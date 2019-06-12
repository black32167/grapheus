/**
 * 
 */
package org.grapheus.cli.remote;

import java.util.Optional;

import javax.inject.Inject;

import org.grapheus.cli.model.CLIAccount;
import org.grapheus.cli.security.CLIUserContext;
import org.grapheus.client.GrapheusClientFactory;
import org.grapheus.client.UserClient;
import org.grapheus.client.api.UserManagementAPI;
import org.grapheus.client.http.auth.GrapheusClientCredentials;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author black
 */
@Service
@RequiredArgsConstructor(onConstructor=@__({@Inject}))
@Slf4j
public class ServerAccountManager {
    private final GrapheusClientFactory clientFactory;
    private final CLIUserContext userCtx;
    
    public void create(CLIAccount account, boolean recreate) {

        String userId = Optional.ofNullable(account.getAccontName()).orElseGet(() -> userCtx.getUserName());
        byte[] password = Optional.ofNullable(account.getPassword()).orElseGet(() -> userCtx.getPassword());
        
        UserClient userClient = clientFactory.forUser(() -> new GrapheusClientCredentials(userId, password));
        UserManagementAPI userAPI = userClient.user();
        if(recreate) {
            try {
                userAPI.delete(userId);
            } catch (Exception e) {
                log.warn("Cannot delete user {}:{}", account.getAccontName(), e.getMessage());
            }
        }
        
        clientFactory.userCreator().createUser(userId, password);
    }

    public void delete(String accountName) {
        clientFactory.//
            forUser(() -> new GrapheusClientCredentials(accountName, userCtx.getPassword())).//
            user().//
            delete(accountName);
    }
}
