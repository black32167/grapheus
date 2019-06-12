/**
 * 
 */
package grapheus.rest.resource.graph.operation;

import java.net.URI;
import java.net.URISyntaxException;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.grapheus.client.model.graph.VerticesSortCriteriaType;
import org.grapheus.client.model.graph.generate.RGraphCreationParameters;
import org.grapheus.client.model.graph.generate.RMergeRequest;
import org.grapheus.client.model.graph.generate.RPathGraphParameters;

import grapheus.context.GrapheusRequestContextHolder;
import grapheus.graph.GraphsManager;
import grapheus.persistence.exception.GraphExistsException;
import grapheus.persistence.graph.generate.CloneGraphGenerator;
import grapheus.persistence.graph.generate.CyclesGraphGenerator;
import grapheus.persistence.graph.generate.EmptyGraphGenerator;
import grapheus.persistence.graph.generate.FeatureSubgraphGraphGenerator;
import grapheus.persistence.graph.generate.PathsGraphGenerator;
import grapheus.persistence.graph.generate.SelfGraphGenerator;
import grapheus.persistence.graph.generate.TraversalGraphGenerator;
import grapheus.persistence.storage.graph.transaction.merge.MergeVerticesTransaction;
import grapheus.persistence.storage.graph.transaction.topology.TopologicalMarkingTransaction;

/**
 * @author black
 *
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path(OperationResourcesConstants.PATH)
public class GraphOperationsResource {
    
    @Inject
    private CyclesGraphGenerator cyclesGraphGenerator;
    @Inject
    private FeatureSubgraphGraphGenerator featureSubgraphGenerator;
    @Inject
    private EmptyGraphGenerator emptyGraphGenerator;
    @Inject
    private SelfGraphGenerator selfGraphGenerator;
    @Inject
    private CloneGraphGenerator cloneGraphGenerator;
    @Inject
    private TraversalGraphGenerator traversalGraphGenerator;
    @Inject
    private TopologicalMarkingTransaction topologicalMarkingTransaction;
    @Inject
    private MergeVerticesTransaction mergeTransaction;
    @Inject
    private PathsGraphGenerator pathsGraphGenerator;
    @Inject
    private GraphsManager graphsManager;
    
    @POST
    @Path("pathGraph")
    public Response findPaths(RPathGraphParameters findPathsRequest) throws GraphExistsException, URISyntaxException {
        pathsGraphGenerator.generate(grapheusUserKey(),
                findPathsRequest.getSourceGraphName(),
                findPathsRequest.getNewGraphName(),
                findPathsRequest.getBoundaryVerticesIds());
        return createdResponse(findPathsRequest.getNewGraphName());
    }
    
    @POST
    @Path("merge")
    public Response mergeVertices(RMergeRequest mergeRequest) throws Exception {
        mergeTransaction.merge(
                grapheusUserKey(),
                mergeRequest.getGraphId(),
                mergeRequest.getNewVertexName(),
                mergeRequest.getVerticesIds());
        return createdResponse(mergeRequest.getGraphId());
    }
    
    @POST
    @Path("cyclicGraph")
    public Response generateCyclesSubraph(@QueryParam("sourceGraphId") String sourceGraphId, RGraphCreationParameters parameters) throws Exception {
        try {
            cyclesGraphGenerator.generateCyclesGraph(grapheusUserKey(), sourceGraphId, parameters.getNewGraphName());
            return createdResponse(parameters.getNewGraphName());
        } catch (GraphExistsException e) {
            throw new WebApplicationException("Graph already exists:'" + parameters.getNewGraphName() + "'", Response.Status.CONFLICT);
        }
    }


    @POST
    @Path("selfGraph")
    public Response generateSelfGraph(RGraphCreationParameters parameters) throws Exception {
        try {
            selfGraphGenerator.generate(grapheusUserKey(), parameters.getNewGraphName());
            return createdResponse(parameters.getNewGraphName());
        } catch (GraphExistsException e) {
            throw new WebApplicationException("Graph already exists:'" + parameters.getNewGraphName() + "'", Response.Status.CONFLICT);
        }
    }
    

    @POST
    @Path("emptyGraph")
    public Response generateEmptyGraph(RGraphCreationParameters parameters) throws Exception {
        try {
            emptyGraphGenerator.createGraph(grapheusUserKey(), parameters.getNewGraphName());
            return createdResponse(parameters.getNewGraphName());
        } catch (GraphExistsException e) {
            throw new WebApplicationException("Graph already exists:'" + parameters.getNewGraphName() + "'", Response.Status.CONFLICT);
        }
    }
    

    @POST
    @Path("cloneGraph")
    public Response generateCloneGraph(@QueryParam("sourceGraphId") String sourceGraphId, RGraphCreationParameters parameters) throws Exception {
        try {
            cloneGraphGenerator.generate(grapheusUserKey(), sourceGraphId, parameters.getNewGraphName());
            return createdResponse(parameters.getNewGraphName());
        } catch (GraphExistsException e) {
            throw new WebApplicationException("Graph already exists:'" + parameters.getNewGraphName() + "'", Response.Status.CONFLICT);
        }
    }

    @POST
    @Path("traversalGraph")
    public Response generateTraversalSubgraph(@QueryParam("sourceGraphId") String sourceGraphId, RGraphCreationParameters parameters) throws Exception {
        try {
            traversalGraphGenerator.generate(
                    grapheusUserKey(), 
                    sourceGraphId,
                    parameters.getNewGraphName(),
                    parameters.getStartingVertex(),
                    parameters.getTraversalDirection());
            return createdResponse(parameters.getNewGraphName());
        } catch (GraphExistsException e) {
            throw new WebApplicationException("Graph already exists:'" + parameters.getNewGraphName() + "'", Response.Status.CONFLICT);
        }
    }
    

    @POST
    @Path("propertyGraph")
    public Response generatePropertyBased(@QueryParam("sourceGraphId") String sourceGraphId, RGraphCreationParameters parameters) throws Exception {
        try {
            featureSubgraphGenerator.generate(
                    grapheusUserKey(), 
                    sourceGraphId,
                    parameters.getNewGraphName(),
                    parameters.getSourceProperty());
            return createdResponse(parameters.getNewGraphName());
        } catch (GraphExistsException e) {
            throw new WebApplicationException("Graph already exists:'" + parameters.getNewGraphName() + "'", Response.Status.CONFLICT);
        }
    }
    


    @POST
    @Path("topologicalSort")
    public Response generateTopologicalSort(@QueryParam("sourceGraphId") String sourceGraphId) throws Exception {

        topologicalMarkingTransaction.topologicalOrder(sourceGraphId);
        graphsManager.addOperationApplied(sourceGraphId, VerticesSortCriteriaType.TOPOLOGICAL.name());
    
        return createdResponse(sourceGraphId);
    }
    
    private Response createdResponse(String newGraphName) throws URISyntaxException {
        return Response.created(new URI("http://blah.org")).build();//TODO: return reasonable url;
    }

    private String grapheusUserKey() {
        return GrapheusRequestContextHolder.getContext().getUserId();
    }
}
