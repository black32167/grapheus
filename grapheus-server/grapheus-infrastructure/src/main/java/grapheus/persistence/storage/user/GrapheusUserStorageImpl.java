/**
 * 
 */
package grapheus.persistence.storage.user;

import java.util.List;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.arangodb.ArangoDatabase;

import lombok.RequiredArgsConstructor;
import grapheus.TimeService;
import grapheus.event.UserDeleteListener;
import grapheus.persistence.StorageSupport;
import grapheus.persistence.exception.DocumentExistsException;
import grapheus.persistence.exception.DocumentNotFoundException;
import grapheus.persistence.model.ModelMeta;
import grapheus.persistence.model.personal.GrapheusUser;
import grapheus.utils.ListenerUtils;

/**
 * @author black
 *
 */
@Repository
 @RequiredArgsConstructor(onConstructor= @__({@Inject}))
public class GrapheusUserStorageImpl  extends StorageSupport implements GrapheusUserStorage {
    private final TimeService timer;
    private final static String USER = ModelMeta.getCollectionName(GrapheusUser.class);
    
    @Autowired(required = false)
    private List<UserDeleteListener> userLifecycleListeners;

    @Override
    public void updateUser(String userKey, Consumer<GrapheusUser> consumer) {
        updateDocument(USER, userKey, GrapheusUser.class, consumer);
    }

    @Override
    public GrapheusUser getUser(String userId) {
        GrapheusUser user = getDocument(USER, userId, GrapheusUser.class);
        if(user == null) {
            throw new DocumentNotFoundException("Cannot get user by key " + userId);
        }
        return user;
    }
    
    @Override
    public String createUser(GrapheusUser user) throws UserExistsException {
        user.setCreationTimestamp(timer.getMills());
        try {
            return createDocument(USER, user);
        } catch (DocumentExistsException e) {
            throw new UserExistsException();
        }
    }

    @Override
    public List<GrapheusUser> getAllUsers(int start, int limit) {
        return listAll(USER, GrapheusUser.class, start, limit);
    }

    @Override
    public void deleteUser(String userKey) {
        ListenerUtils.iterateLogExceptions(userLifecycleListeners, e -> e.onDeleteUser(userKey));
        
        deleteDocument(USER, userKey);
    }

    @Override
    public boolean exists(String userKey) {
        return documentExists(USER, userKey);
    }

    @Override
    public void updateCredentialsHash(String userKey, byte[] hash) {
        updateUser(userKey, oldCreds -> {
            oldCreds.setHash(hash);
        });
    }
    
    @Override
    public List<GrapheusUser> list(List<String> userIds) {
        return query().from(GrapheusUser.class).filter(GrapheusUser.FIELD_ID, "IN", userIds).list();
    }
    
    @Override
    public byte[] getCredentialsHash(String userId) {
        GrapheusUser user = getUser(userId);
        
        return user.getHash();
    }
    
    @Override
    public void onConnected(ArangoDatabase db) {
        createCollection(db, GrapheusUser.class);
    }
}
