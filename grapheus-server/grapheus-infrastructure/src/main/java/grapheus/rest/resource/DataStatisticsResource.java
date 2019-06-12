/**
 * 
 */
package grapheus.rest.resource;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.grapheus.client.model.RDataStatisticsContainer;

import grapheus.context.GrapheusRequestContextHolder;
import grapheus.graph.GraphsManager;

/**
 * @author black
 *
 */
@Path(DataStatisticsResource.PATH)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DataStatisticsResource {
    public final static String PATH = "/dstat";

    @Inject
    private GraphsManager artifactsManager;

    @GET
    public RDataStatisticsContainer getDataStatistics() {
        String userKey = GrapheusRequestContextHolder.getContext().getUserId();
        int aCount = artifactsManager.getUserArtifactsCount(userKey);
        return RDataStatisticsContainer.builder().artifactsCount(aCount).build();
    }
}
