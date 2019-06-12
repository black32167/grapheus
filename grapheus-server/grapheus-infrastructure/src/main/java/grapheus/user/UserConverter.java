/**
 * 
 */
package grapheus.user;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.grapheus.client.model.RUser;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import grapheus.persistence.model.personal.GrapheusUser;
import grapheus.security.credentials.HashService;

/**
 * @author black
 *
 */
@Service
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class UserConverter {
    private final HashService hashService;
    
    public GrapheusUser toPersistentUser(RUser restUser) {
        return GrapheusUser.builder().
            name(restUser.getName()).//
            hash(hashService.hash(restUser.getPassword())).//
            build();
    }
    
    public RUser toRESTUser(GrapheusUser user) {
        return RUser.builder()
                .name(user.getName())
                .build();
    }

    public List<RUser> toRESTUsers(List<GrapheusUser> owners) {
        List<RUser> externalUsers = new ArrayList<>();
        for(GrapheusUser internalUser:owners) {
            externalUsers.add(toRESTUser(internalUser));
        }
        return externalUsers;
    }
}
