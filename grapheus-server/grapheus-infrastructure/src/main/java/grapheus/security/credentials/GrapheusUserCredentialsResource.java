/**
 * 
 */
package grapheus.security.credentials;

import javax.inject.Inject;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.grapheus.client.model.security.RUserCredentials;

import lombok.NonNull;
import lombok.Setter;
import grapheus.persistence.storage.user.GrapheusUserStorage;
@Produces(MediaType.APPLICATION_JSON)
public class GrapheusUserCredentialsResource {

    @NonNull
    @Setter
    private String userKey;
    
    @Inject
    private GrapheusUserStorage userStorge;
    
    @Inject
    private HashService hashService;
    
    @PUT
    public Response updateUserCredentials(RUserCredentials basicCredentials) {

        // Actually update grapheus user credentials
        byte[] hash = hashService.hash(basicCredentials.getUserSecret());
        userStorge.updateCredentialsHash(userKey, hash);
        
        return Response.noContent().build();
    }
    
}
