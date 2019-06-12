/**
 * 
 */
package grapheus.rest.resource.graph;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.grapheus.client.model.RGraphInfo;
import org.grapheus.client.model.graph.RGraph;
import org.grapheus.client.model.graph.RGraphsContainer;
import org.grapheus.client.model.graph.VerticesSortCriteriaType;
import org.grapheus.client.model.graph.edge.REdge;
import org.grapheus.client.model.graph.vertex.RVertex;

import grapheus.context.GrapheusRequestContextHolder;
import grapheus.graph.GraphMetaInfo;
import grapheus.graph.GraphsManager;
import grapheus.graph.bulk.EdgeBulkImporterFactory;
import grapheus.graph.bulk.EdgeBulkImporterFactory.EdgeBulkImporter;
import grapheus.graph.bulk.VertexBulkImporterFactory;
import grapheus.graph.bulk.VertexBulkImporterFactory.VertexBulkImporter;
import grapheus.persistence.exception.GraphExistsException;
import grapheus.persistence.storage.graph.GraphNameUtils;
import grapheus.rest.converter.EdgeConverter;
import grapheus.rest.converter.VertexConverter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author black
 *
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path(GraphResource.PATH)
@Slf4j
public class GraphResource {
    public final static String PATH = "/graph";
    
    @Inject
    private GraphsManager graphManager;
    

    @Inject
    private VertexBulkImporterFactory vertexBulkImporterFactory;
    
    @Inject
    private EdgeBulkImporterFactory edgeBulkImporterFactory;
    
    @GET
    //@Path("all")
    public RGraphsContainer getAvailableGraphs() {
        String userKey = GrapheusRequestContextHolder.getContext().getUserId();
        return RGraphsContainer.builder().//
                graphs(graphManager.getUserGraphs(userKey).stream().map(this::toExternalGraph).collect(Collectors.toList())).//
                build();
    }
    

    @GET
    @Path("{graphId}")
    public RGraphInfo getInfo(@PathParam("graphId") String graphId) {
        String grapheusUserKey = GrapheusRequestContextHolder.getContext().getUserId();
        int verticesCount = graphManager.getArtifactsCount(grapheusUserKey, graphId);
        List<VerticesSortCriteriaType> availableSortCriteria  = graphManager.getAvailableSortingCriteria(graphId);
        
        return RGraphInfo.builder()
                .verticesCount(verticesCount)
                .availableSortCriteria(availableSortCriteria)
                .build();
    }
    
    @DELETE
    @Path("{graphId}")
    public Response deleteGraph(@PathParam("graphId") String graphId) throws InterruptedException, ExecutionException {
        
        String grapheusUserKey = GrapheusRequestContextHolder.getContext().getUserId();
        
        graphManager.deleteGraph(grapheusUserKey, graphId);
        
        return Response.noContent().build();
    }

    @POST
    @Consumes("application/zip")
    @Path("{graphId}/import")
    public Response importGraph(@PathParam("graphId") String graphId, InputStream graphStream) throws Exception {
        String grapheusUserKey = GrapheusRequestContextHolder.getContext().getUserId();
        
        log.info("Creating graph {}", graphId);
        
        createGraph(graphId);
        
        final String verticesCollection = GraphNameUtils.verticesCollectionName(graphId);
        try (
                VertexBulkImporter vertexImporter = vertexBulkImporterFactory.newVertexImporter(grapheusUserKey, graphId);
                EdgeBulkImporter edgeImporter = edgeBulkImporterFactory.newEdgeImporter(grapheusUserKey, graphId)) {
            GraphStreamParser.builder()
                .edgeConsumer(e -> edgeImporter.importEdge(EdgeConverter.toInternal(verticesCollection, e)))
                .vertexConsumer(v -> vertexImporter.importVertex(VertexConverter.toInternal(v)))
                .build()
                .consumeStream(graphStream);
        }
        
        return Response.created(URI.create("/"+graphId)).build();//TODO
    }
    
    private void createGraph(String graphId) {
        String grapheusUserKey = GrapheusRequestContextHolder.getContext().getUserId();
        try {
            graphManager.createGraphForUser(grapheusUserKey, graphId);
        } catch (GraphExistsException e) {
            throw new WebApplicationException(e.getMessage(), Status.CONFLICT);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{graphId}/export")
    public Response exportGraph(@PathParam("graphId") String graphId) {
        String grapheusUserKey = GrapheusRequestContextHolder.getContext().getUserId();
        Iterable<RVertex> vertexIterable = () -> graphManager.getAllVertices(grapheusUserKey, graphId).map(VertexConverter::toExternalVertex).iterator();
        Iterable<REdge> edgesIterable = () -> graphManager.getAllEdges(grapheusUserKey, graphId).map(EdgeConverter::toExternalEdge).iterator();
        return Response
                .ok(new StreamingOutput() {
                    @Override
                    public void write(OutputStream output) throws IOException, WebApplicationException {
                        GraphStreamSerializer.builder()//
                                    .graphId(graphId)//
                                    .verticesProducer(vertexIterable)
                                    .edgesProducer(edgesIterable)
                                    .build()
                                    .serialize(output);
                    }
                })
                .type("application/zip")
                .header("Content-Disposition", "inline; filename="+graphId+".zip")
                .build();
    }


    private RGraph toExternalGraph(GraphMetaInfo graphInfo) {
        return RGraph.builder().//
                name(graphInfo.getName()).//
                editPermitted(graphInfo.isHasEditPermissions()).
                build();
    }
    

}
