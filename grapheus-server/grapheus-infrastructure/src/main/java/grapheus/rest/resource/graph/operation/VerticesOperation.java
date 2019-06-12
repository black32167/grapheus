/**
 * 
 */
package grapheus.rest.resource.graph.operation;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.grapheus.client.model.graph.operation.RVerticesRemoveOperationContainer;
import org.grapheus.client.model.graph.operation.path.RPathContainer;
import org.grapheus.client.model.graph.operation.path.RShortestPathParameters;

import grapheus.persistence.graph.calculate.PathCalculator;
import grapheus.persistence.storage.graph.VertexStorage;
import grapheus.rest.ResourceURIFactory;

/**
 * @author black
 *
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path(OperationResourcesConstants.PATH+"/vertices")
public class VerticesOperation {
    @Inject
    private VertexStorage vertexStorage;
    
    @Inject
    private ResourceURIFactory resourceURIFactory;
    
    @Inject
    private PathCalculator pathCalculator;
    
    @Path("delete")
    @POST
    public Response deleteVertices(RVerticesRemoveOperationContainer removeContainer) {
        vertexStorage.deleteVertices(removeContainer.getGraphId(), removeContainer.getVerticesIds());
        return Response.created(UriBuilder.fromUri(resourceURIFactory.getBaseURI()).path(OperationResourcesConstants.PATH+"/vertices/delete").build()).build();
        
    }
    
    @Path("shortestPath")
    @POST
    public Response findShortestPath(RShortestPathParameters parameters) {
        List<String> pathDirect = pathCalculator.findPath(parameters.getGraphId(), parameters.getFromVertexId(), parameters.getToVertexId());
        List<String> pathBack = pathCalculator.findPath(parameters.getGraphId(), parameters.getToVertexId(), parameters.getFromVertexId());
        List<String> path = (!pathDirect.isEmpty() && !pathBack.isEmpty())
                ? (pathDirect.size() < pathBack.size() ? pathDirect : pathBack)
                : (!pathDirect.isEmpty() ? pathDirect : pathBack);
        return Response
                .created(UriBuilder.fromUri(resourceURIFactory.getBaseURI()).path(OperationResourcesConstants.PATH+"/vertices/shortestPath").build())
                .entity(RPathContainer.builder().pathVericesIds(path).build())
                .build();
    }
}
