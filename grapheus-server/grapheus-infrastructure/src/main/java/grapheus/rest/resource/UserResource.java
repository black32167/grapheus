/**
 * 
 */
package grapheus.rest.resource;

import static java.lang.String.format;

import java.net.URI;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.internal.util.Base64;
import org.grapheus.client.model.RUser;
import org.grapheus.client.model.RUserList;

import lombok.extern.slf4j.Slf4j;
import grapheus.context.GrapheusRequestContextHolder;
import grapheus.persistence.model.personal.GrapheusUser;
import grapheus.persistence.storage.user.UserExistsException;
import grapheus.security.credentials.GrapheusUserCredentialsResource;
import grapheus.user.UserConverter;
import grapheus.user.UserManager;

/**
 * @author black
 *
 */
@Path(UserResource.PATH)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class UserResource {
    public final static String PATH = "/user";
    
    @Inject
    private UserManager userManager;
    
    @Inject
    private ResourceContext context;
    
    @Inject
    private UserConverter userConverter;
    
    @POST
    public Response addUser(RUser user) {
        String userId;
        
//        try {
//            String t = new String(user.getPassword(), "UTF-8");
//            System.out.println(t);
//        } catch (UnsupportedEncodingException e1) {
//            e1.printStackTrace();
//        }
        String pwd = Base64.decodeAsString(user.getPassword());//, BasicAuthUtil.BASIC_ENCODING);
        
        try {
            userId = userManager.createUser(userConverter.toPersistentUser(user));
        } catch (UserExistsException e) {
            throw new WebApplicationException("User already exists:" + user.getName(), Status.CONFLICT);
        }
        return Response.created(URI.create(format("%s/%s", PATH, userId))).build();
    }

    @POST
    @Path("check")
    public Response checkUser(RUser user) {
        log.debug("Checking authentication of user {}", user.getName());
        try {
            if(!userManager.userExists(user.getName(), user.getPassword())) {
               return Response.status(Status.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
            log.error("Error authenticating user {}", user.getName(), e);
            return Response.serverError().build();
        }
        return Response.created(URI.create(format("%s/%s", PATH, user.getName()))).build();
    }

    @GET
    public RUserList listUsers(
            @DefaultValue("0") @QueryParam("start") int start,
            @DefaultValue("100") @QueryParam("limit") int limit) {
        
        List<GrapheusUser> users = userManager.getAllUsers(start, limit);
        
        return RUserList.builder().//
                users(userConverter.toRESTUsers(users)).
                build();
    }

    @DELETE
    @Path("{userKey}")
    public void deleteUser(@PathParam("userKey") String userKey) {
        if(!Objects.equals(
                GrapheusRequestContextHolder.getContext().getUserId(),
                userKey)) {
            throw new BadRequestException("Trying to delete foreighn user");
        }
        userManager.deleteUser(userKey);
    }

    @Path("{userKey}/credentials")
    public GrapheusUserCredentialsResource userCredentials(@PathParam("userKey") String userKey) {
        GrapheusUserCredentialsResource sourceResource = context.getResource(GrapheusUserCredentialsResource.class);
        sourceResource.setUserKey(userKey);
        return sourceResource;
    }
    
}
