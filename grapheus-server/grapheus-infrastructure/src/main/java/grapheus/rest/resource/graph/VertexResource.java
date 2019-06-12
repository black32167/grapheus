/**
 * 
 */
package grapheus.rest.resource.graph;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.grapheus.client.model.RUserList;
import org.grapheus.client.model.graph.RPropertiesContainer;
import org.grapheus.client.model.graph.VertexInfoType;
import org.grapheus.client.model.graph.VerticesSortCriteria;
import org.grapheus.client.model.graph.edge.EdgeDirection;
import org.grapheus.client.model.graph.edge.RAdjacentEdgesFilter;
import org.grapheus.client.model.graph.edge.REdge;
import org.grapheus.client.model.graph.edge.REdgesContainer;
import org.grapheus.client.model.graph.search.RSearchRequest;
import org.grapheus.client.model.graph.vertex.RVertex;
import org.grapheus.client.model.graph.vertex.RVertexInfo;
import org.grapheus.client.model.graph.vertex.RVertexInfosContainer;
import org.grapheus.client.model.graph.vertex.RVerticesContainer;

import grapheus.TimeService;
import grapheus.context.GrapheusRequestContextHolder;
import grapheus.exception.PermissionDeniedException;
import grapheus.graph.GraphsManager;
import grapheus.graph.VertexInfoCalculatorManager;
import grapheus.graph.bulk.VertexBulkImporterFactory;
import grapheus.graph.bulk.VertexBulkImporterFactory.VertexBulkImporter;
import grapheus.persistence.exception.CollectionNotFoundException;
import grapheus.persistence.graph.calculate.CalculatedVertexInfo;
import grapheus.persistence.model.graph.PersistentVertex;
import grapheus.persistence.model.personal.GrapheusUser;
import grapheus.persistence.storage.graph.query.VertexFinder.SearchResult;
import grapheus.persistence.storage.traverse.Edge;
import grapheus.rest.converter.VertexConverter;
import grapheus.service.uds.ArtifactsFilter;
import grapheus.user.UserConverter;
import grapheus.utils.EntityIdUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @author black
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path(VertexResource.PATH)
@Slf4j
public class VertexResource {
    public final static String PATH = "/graph/{graphId}/vertex";

    private static final int ARTIFACTS_RETURN_LIMIT = 50;

    @PathParam("graphId")
    private String graphName;
    
    @Inject
    private GraphsManager graphsManager;
    
    @Inject
    private VertexBulkImporterFactory vertexBulkImporterFactory;
    
    @Inject VertexInfoCalculatorManager vertexInfoCalculator;
    
    @Inject
    private TimeService ts;
    
    @Inject
    private UserConverter userConverter;
    
    

    @GET
    @Path("/properties")//TODO: is it used?
    public RPropertiesContainer getProperties() {
        String grapheusUserKey = GrapheusRequestContextHolder.getContext().getUserId();
        return RPropertiesContainer.builder().//
                properties(graphsManager.getAllArtifactsProperties(grapheusUserKey, graphName)).//
                build();
    }
    
    // TODO: can we just use 'bulk' version always?
    @PUT
    public void add(RVertex remoteVertexModel) {
        long start = System.currentTimeMillis();
        validate(remoteVertexModel);// Check that artifact is valid as well as user permissions
        
        PersistentVertex internalArtifact = VertexConverter.toInternal(remoteVertexModel);
        
        String grapheusUserKey = GrapheusRequestContextHolder.getContext().getUserId();
        try {
            graphsManager.update(grapheusUserKey, graphName, internalArtifact);
        } catch (CollectionNotFoundException e) {
            throw new NotFoundException("Graph '" + graphName + "' does not exist");
        }
        
        long end = System.currentTimeMillis();
        log.debug("Added vertex {} in {} ms", remoteVertexModel.getTitle(), end-start);
    }

    @PUT
    @Path("batch")
    public void add(RVerticesContainer vertiesContainer) throws Exception {
        long start = System.currentTimeMillis();
        Collection<RVertex> vertices = vertiesContainer.getArtifacts();
        if(vertices != null) {
            String grapheusUserKey = GrapheusRequestContextHolder.getContext().getUserId();
            try (VertexBulkImporter vertexImporter = vertexBulkImporterFactory.newVertexImporter(grapheusUserKey, graphName)) {
                vertices.forEach(v-> {
                    vertexImporter.importVertex(VertexConverter.toInternal(validate(v)));
                });
            }
            
            long end = System.currentTimeMillis();
            log.debug("Added {} verties in {} ms", vertices.size(), end-start);
        }

        
    }
    


    
    @PUT
    @Path("{artifactId}/update")
    public void update(@PathParam("artifactId") String artifactId, RVertex artifact) {
      
        PersistentVertex internalArtifact = new PersistentVertex();
        internalArtifact.setTitle(artifact.getTitle());
        internalArtifact.setDescription(artifact.getDescription());
        internalArtifact.setExternalCompositeId(artifactId);
         
        String grapheusUserKey = GrapheusRequestContextHolder.getContext().getUserId();
        try {
            graphsManager.partialUpdate(grapheusUserKey, graphName, internalArtifact);
        } catch (CollectionNotFoundException e) {
            throw new NotFoundException("Graph '" + graphName + "' does not exist");
        }
        
    }
    

    @POST
    @Path("/search")
    public Response search(RSearchRequest request) throws InterruptedException, ExecutionException {
        
        ArtifactsFilter dataFilter = ArtifactsFilter.builder().//
                user(request.getFilteringUser()).
                title(request.getTitle()).//
                limit(ARTIFACTS_RETURN_LIMIT).//
                sinks(request.getSinks() != null && request.getSinks()).//
                artifactKeys(request.getVerticesIds() == null || request.getVerticesIds().isEmpty() ? null : request.getVerticesIds()).//
                minimalAdjacentEdgesFilter(RAdjacentEdgesFilter.deserialize(request.getMinEdgesSpec())).
                build();

        List<VerticesSortCriteria> sortingCriteria = VerticesSortCriteria.deserializeSortingCriteria(request.getSortingCriteriaSpec());
        String grapheusUserKey = GrapheusRequestContextHolder.getContext().getUserId();
        
        SearchResult verticesSearchResult = graphsManager.//
                findVerticesByCriteria(grapheusUserKey, graphName, dataFilter, sortingCriteria);
        boolean editableGraph = graphsManager.hasEditUserPermissions(graphName, grapheusUserKey);
        
        return Response.created(URI.create(format("/%s", "search")))
                .entity(RVerticesContainer.builder().
                    artifacts(verticesSearchResult.getVertices().stream().map(VertexConverter::toExternalVertex).collect(Collectors.toList())).
                    editPermitted(editableGraph).
                    totalCount(verticesSearchResult.getTotalCount()).
                    build())
                .build();
    }


    @GET
    @Path("/info")
    public RVertexInfosContainer calculateVerticesInfo(
            @QueryParam("ids") String vertexIds,
            @QueryParam("typeSpec") String infoTypeSpec) {
        String grapheusUserKey = GrapheusRequestContextHolder.getContext().getUserId();
        VertexInfoType vertexInfoType = VertexInfoType.valueOf(infoTypeSpec);
        List<CalculatedVertexInfo> calculatedInfos = vertexInfoCalculator.verticesInfo(grapheusUserKey, graphName, vertexInfoType, Arrays.asList(vertexIds.split(",")));
        
        return RVertexInfosContainer.builder().
                infos(calculatedInfos.stream().map(this::toExternalVertexInfo).collect(Collectors.toList())).
                build();
    }
    
    @GET
    @Path("/multiple")
    public RVerticesContainer loadArtifacts(
            @QueryParam("artifactsKeys") String keys) throws InterruptedException, ExecutionException {

        String grapheusUserKey = GrapheusRequestContextHolder.getContext().getUserId();
        
        Collection<PersistentVertex> foundArtifacts = graphsManager.load(grapheusUserKey, graphName, Arrays.asList(keys.split(",")));

        boolean editableGraph = graphsManager.hasEditUserPermissions(graphName, grapheusUserKey);
        return RVerticesContainer.builder().
                artifacts(foundArtifacts.stream().map(VertexConverter::toExternalVertex).collect(Collectors.toList())).
                editPermitted(editableGraph).
                build();
    }
    
    @GET
    @Path("{artifactId}")
    public RVertex getArtifact(
            @PathParam("artifactId") String artifactId) throws InterruptedException, ExecutionException {
        
        String grapheusUserKey = GrapheusRequestContextHolder.getContext().getUserId();
        
        try {
            PersistentVertex artifact = graphsManager.getArtifact(grapheusUserKey, graphName, artifactId).//
                    orElseThrow(() -> new NotFoundException("Artifact '" + artifactId + "' is not found"));
            return VertexConverter.toExternalVertex(artifact);
        } catch (PermissionDeniedException e) {
            throw new ForbiddenException();
        }
        

    }
    
    @DELETE
    @Path("{artifactId}")
    public Response deleteArtifact(
            @PathParam("artifactId") String artifactId) throws InterruptedException, ExecutionException {
        
        String grapheusUserKey = GrapheusRequestContextHolder.getContext().getUserId();
        
        graphsManager.deleteVertex(grapheusUserKey, graphName, artifactId);
        
        return Response.noContent().build();
    }
    
    @DELETE
    @Path("rogue")
    public Response deleteRogueVertices() throws InterruptedException, ExecutionException {
        
        String grapheusUserKey = GrapheusRequestContextHolder.getContext().getUserId();
        
        graphsManager.deleteRogueVertices(grapheusUserKey, graphName);
        
        return Response.noContent().build();
    }
    
    @GET
    @Path("{artifactId}/owners")
    // TODO: deprecate in UI
    public RUserList getArtifactOwners(@PathParam("artifactId") String artifactId) throws InterruptedException, ExecutionException {
        
        List<GrapheusUser> owners = Collections.emptyList();
       
        return RUserList.builder().users(userConverter.toRESTUsers(owners)).build();
    }

    @GET
    @Path("{artifactId}/neighbors")
    public REdgesContainer findNeighbors(
            @PathParam("artifactId") String artifactId,
            @QueryParam("direction") @DefaultValue("OUTBOUND") String directionSpec,
            @QueryParam("depth") @DefaultValue("1") int depth) throws InterruptedException, ExecutionException {
        String grapheusUserKey = GrapheusRequestContextHolder.getContext().getUserId();
        EdgeDirection direction = EdgeDirection.valueOf(directionSpec);
        Collection<Edge> edges = graphsManager.findNeighbors(grapheusUserKey, graphName, artifactId, direction, depth);

        return REdgesContainer.builder().
                edges(edges.stream().map(this::toExternalEdge).collect(Collectors.toList())).
                build();
    }

    private REdge toExternalEdge(Edge edge) {
        return REdge.builder().//
                from(edge.getFrom()).//
                to(edge.getTo()).//
                build();
    }
    
    private RVertexInfo toExternalVertexInfo(CalculatedVertexInfo internalVInfo) {
        return RVertexInfo.builder().
                vertexKey(EntityIdUtils.toKey(internalVInfo.getVertexId())).
                infoData(internalVInfo.getSerializedInfo()).
                build();
    }
    
    

    private RVertex validate(RVertex vertex) {

        try {
           // requireNonNull(vertex.getDescription(), "description");
            requireNonNull(vertex.getTitle(), "title");
        } catch (NullPointerException e) {
            throw new BadRequestException("Field is not set:" + e.getMessage());
        }
        return vertex;
    }

}
