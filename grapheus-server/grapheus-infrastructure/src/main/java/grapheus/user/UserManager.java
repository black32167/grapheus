/**
 * 
 */
package grapheus.user;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import grapheus.persistence.model.personal.GrapheusUser;
import grapheus.persistence.storage.user.GrapheusUserStorage;
import grapheus.persistence.storage.user.UserExistsException;
import grapheus.security.credentials.HashService;

/**
 * @author black
 */
@Service
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
@Slf4j
public class UserManager {
    private final GrapheusUserStorage userStorage;
    private final HashService hashService;
    
    public void deleteUser(String userKey) {
        log.info("Deleting user {}", userKey);
        userStorage.deleteUser(userKey);
    }

    public String createUser(GrapheusUser user) throws UserExistsException {
        log.info("Creating user {}", user.getName());
        
        String userKey = userStorage.createUser(user);
        
        return userKey;
    }

    public List<GrapheusUser> getAllUsers(int start, int limit) {
        return userStorage.getAllUsers(start, limit);
    }


    public boolean userExists(String userKey, byte[] password) {
        byte[] chashStored = userStorage.getCredentialsHash(userKey);
        byte[] chashInput = hashService.hash(password);
        return Arrays.equals(chashStored, chashInput);
    }
}
