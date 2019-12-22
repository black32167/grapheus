/**
 * 
 */
package grapheus.rest.resource;

import grapheus.context.GrapheusRequestContextHolder;
import grapheus.persistence.storage.graph.query.EdgesFinder;
import grapheus.persistence.storage.graph.query.VertexFinder;
import grapheus.persistence.storage.graph.transaction.bridge.BridgesSearchTransaction;
import grapheus.rest.converter.EdgeConverter;
import org.grapheus.client.model.graph.GraphNamesConstants;
import org.grapheus.client.model.graph.edge.REdge;
import org.grapheus.client.model.graph.edge.REdgesContainer;
import org.grapheus.client.model.graph.vertex.RVerticesIdsContainer;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author black
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path(ComputeResource.PATH)
public class ComputeResource {
    public final static String PATH = "/compute";
    private final static int DEFAULT_DEPTH = 30;
    private final static String DEFAULT_GRAPH_NAME = GraphNamesConstants.DEFAULT_GRAPH_NAME;//TODO: specify graph name on client
    
    @Inject
    private EdgesFinder edgesFinder;

    @Inject
    private VertexFinder vertexFinder;
    
    @Inject
    private BridgesSearchTransaction bridgesTransaction;

    @GET
    @Path("bridges")
    public REdgesContainer findBridges() {
        String userKey = GrapheusRequestContextHolder.getContext().getUserId();

        return REdgesContainer.builder().//
                edges(bridgesTransaction.bridges(DEFAULT_GRAPH_NAME).stream().//
                        map(EdgeConverter::toREdge).//
                        collect(Collectors.toList())).//
                build();
    }
    
    @GET
    @Path("sinks")
    public RVerticesIdsContainer findSinks() {
        String userKey = GrapheusRequestContextHolder.getContext().getUserId();
        return RVerticesIdsContainer.builder().//
                vertices(vertexFinder.findSinks(DEFAULT_GRAPH_NAME)).//
                build();
    }

    @GET
    @Path("outbound")
    public REdgesContainer findOutboundDependencies(
            @QueryParam("rootVerticesIds") String rootVerticesIds,
            @DefaultValue(""+DEFAULT_DEPTH) @QueryParam("traversalDepth") Integer depth) {
        if(rootVerticesIds == null) {
            throw new BadRequestException("rootVerticesIds parameter is not specified");
        }
        
        String userKey = GrapheusRequestContextHolder.getContext().getUserId();
        List<REdge> redges = edgesFinder.outbound(DEFAULT_GRAPH_NAME, Arrays.asList(rootVerticesIds.split(",")), depth).stream().//
                map(EdgeConverter::toREdge).collect(Collectors.toList());
        return REdgesContainer.builder().//
                edges(redges).//
                build();
    }
    
     
}
