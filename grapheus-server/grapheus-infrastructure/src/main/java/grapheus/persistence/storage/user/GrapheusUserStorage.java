/**
 * 
 */
package grapheus.persistence.storage.user;

import java.util.List;
import java.util.function.Consumer;

import grapheus.persistence.model.personal.GrapheusUser;
/**
 * @author black
 */

public interface GrapheusUserStorage {
  
    public void updateUser(String userKey, Consumer<GrapheusUser> consumer);
    public GrapheusUser getUser(String userId);
    
    public String createUser(GrapheusUser user) throws UserExistsException;

    public List<GrapheusUser> getAllUsers(int start, int limit);

    public void deleteUser(String userKey) ;

    public boolean exists(String userKey);

    public void updateCredentialsHash(String userKey, byte[] hash) ;
    
    public List<GrapheusUser> list(List<String> userIds);
    
    public byte[] getCredentialsHash(String userId);
    
}
