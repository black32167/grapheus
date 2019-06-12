/**
 * 
 */
package grapheus.rest.resource.graph.operation;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.grapheus.client.model.graph.generate.RConnectionRequest;
import org.grapheus.client.model.graph.generate.RDisconnectionRequest;

import grapheus.persistence.storage.graph.EdgeStorage;
import grapheus.rest.ResourceURIFactory;

/**
 * @author black
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path(OperationResourcesConstants.PATH)
public class EdgesOperations {
    
    @Inject
    private EdgeStorage edgeStorage; 
    
    @Inject
    private ResourceURIFactory resourceURIFactory;
    
    @POST
    @Path("connect")
    public Response connectVertices(RConnectionRequest connectionRequest) {
        for(String fromId: connectionRequest.getFromVerticesIds()) {
            for(String toId: connectionRequest.getToVerticesIds()) {
                edgeStorage.connect(connectionRequest.getGraphId(),  fromId, toId);
            }
        }
        
        return Response.created(UriBuilder.fromUri(resourceURIFactory.getBaseURI()).path(OperationResourcesConstants.PATH+"/connect").build()).build();
    }

    
    @POST
    @Path("disconnect")
    public Response disconnectVertices(RDisconnectionRequest disconnectionRequest) {

        edgeStorage.disconnect(disconnectionRequest.getGraphId(),  disconnectionRequest.getFromVertexId(), disconnectionRequest.getToVertexId());
        return Response.created(UriBuilder.fromUri(resourceURIFactory.getBaseURI()).path(OperationResourcesConstants.PATH+"/disconnect").build()).build();
    }

}
