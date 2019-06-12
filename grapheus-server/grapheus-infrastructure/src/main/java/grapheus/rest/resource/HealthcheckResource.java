/**
 * 
 */
package grapheus.rest.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.grapheus.client.model.RHealthcheckResponse;

/**
 * @author black
 *
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path(HealthcheckResource.PATH)
public class HealthcheckResource {
    public final static String PATH = "/healthcheck";
    
    @GET
    public RHealthcheckResponse getHealth() {
        return RHealthcheckResponse.builder().
                ready(true).
                build();
    }
            

}
