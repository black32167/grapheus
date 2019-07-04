/**
 * 
 */
package grapheus.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.inject.Inject;

import grapheus.persistence.exception.DocumentNotFoundException;
import org.grapheus.client.model.graph.SortDirection;
import org.grapheus.client.model.graph.VerticesSortCriteria;
import org.grapheus.client.model.graph.VerticesSortCriteriaType;
import org.grapheus.client.model.graph.edge.EdgeDirection;
import org.springframework.stereotype.Service;

import grapheus.absorb.VertexPersister;
import grapheus.exception.PermissionDeniedException;
import grapheus.persistence.exception.GraphExistsException;
import grapheus.persistence.model.graph.Graph;
import grapheus.persistence.model.graph.PersistentEdge;
import grapheus.persistence.model.graph.PersistentVertex;
import grapheus.persistence.storage.graph.EdgeStorage;
import grapheus.persistence.storage.graph.GraphStorage;
import grapheus.persistence.storage.graph.VertexStorage;
import grapheus.persistence.storage.graph.query.EdgesFinder;
import grapheus.persistence.storage.graph.query.VertexFinder;
import grapheus.persistence.storage.graph.query.VertexFinder.SearchResult;
import grapheus.persistence.storage.traverse.Edge;
import grapheus.service.uds.ArtifactsFilter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Facade for various operations on graph.
 * 
 * @author black
 */
@Service
@RequiredArgsConstructor(onConstructor = @__({ @Inject }))
@Slf4j
//TODO: add user permissions check (here and everywhere - using AspectJ)
public class GraphsManager {
    private final VertexStorage vertexStorage;
    private final EdgeStorage edgesStorage;
    private final EdgesFinder edgesFinder;
    private final VertexFinder vertexFinder;
    private final GraphStorage graphMetaStorage;
    private final VertexPersister artifactsPersister;

    /**
     * Finds artifacts related to user and groups them in cluster.
     */
    public Collection<PersistentEdge> findNeighbors(String currentUserKey, String graphName, String artifactId, EdgeDirection edgesDirection, int depth) {
        return edgesFinder.getNeighbors(graphName, artifactId, edgesDirection, depth);
    }


    public Optional<PersistentVertex> getArtifact(
            @NonNull String userKey,
            @NonNull String graphName,
            @NonNull String uniqArtifactId)
            throws PermissionDeniedException {
        Optional<PersistentVertex> maybeArtifact = vertexStorage.getById(graphName, uniqArtifactId);

        return maybeArtifact;
    }

    /**
     * Finds artifacts related to user and groups them in cluster.
     */
    public void deleteVertex(
            @NonNull String userKey,
            @NonNull String graphName,
            @NonNull String vertexId) {
        try {
            vertexStorage.deleteVertex(graphName, vertexId);
        } catch (DocumentNotFoundException e) {
            log.debug("Deleting document not found: {}", vertexId);
        }
    }

    public int getUserArtifactsCount(@NonNull String userKey) {
        return graphMetaStorage.getUserGraphs(userKey).stream().//
            map(Graph::getName).//
            mapToInt(vertexStorage::getVerticesCount).//
            sum();
    }

    public int getArtifactsCount(
            @NonNull String userKey,
            @NonNull String graphName) {
        return vertexStorage.getVerticesCount(graphName);
    }

    public Collection<PersistentVertex> load(
            @NonNull String userKey,
            @NonNull String graphName,
            @NonNull Collection<String> artifactKeys) {
        return vertexStorage.get(graphName, artifactKeys);
    }
    
    public SearchResult findVerticesByCriteria(
            @NonNull  String currentUserKey,
            @NonNull String graphName,
            @NonNull ArtifactsFilter artifactsFilter,
            @NonNull List<VerticesSortCriteria> verticesSortCriteria) {
        
        boolean containsTopologicalSortCriteria = verticesSortCriteria.stream().
                anyMatch(c -> c.getSortingType() == VerticesSortCriteriaType.TOPOLOGICAL);
        if(containsTopologicalSortCriteria) {
            boolean containsTitleSortingCriteria = verticesSortCriteria.stream().
                    anyMatch(c -> c.getSortingType() == VerticesSortCriteriaType.VERTEX_TITLE);
            if(!containsTitleSortingCriteria) {
                verticesSortCriteria = new ArrayList<>(verticesSortCriteria);
                verticesSortCriteria.add(new VerticesSortCriteria(VerticesSortCriteriaType.VERTEX_TITLE, SortDirection.ASC));
            }
        }
        
        SearchResult result = vertexFinder
                .findVerticesByCriteria(graphName, artifactsFilter, verticesSortCriteria);
        
        return result;
    }
    

    public List<String> getAllArtifactsProperties(String grapheusUserKey, String graphName) {
        return vertexStorage.getAllArtifactsProperties(graphName);
    }

    public void deleteGraph(@NonNull String userKey, String graphName) {
        vertexStorage.deleteGraph(graphName);
        graphMetaStorage.delete(graphName);
    }


    public void update(String grapheusUserKey, String graphName, PersistentVertex newVertex) {
        artifactsPersister.update(graphName, newVertex); 
    }



    public void partialUpdate(String grapheusUserKey, String graphName, PersistentVertex updatePayload) {
        artifactsPersister.partialUpdate(graphName, updatePayload); 
        
    }
    
    public List<GraphMetaInfo> getUserGraphs(String userKey) {
        return graphMetaStorage.getUserGraphs(userKey).stream()
                .map(ug->GraphMetaInfo.builder()
                        .name(ug.getName())
                        .hasEditPermissions(hasEditUserPermissions(ug, userKey))
                        .build())
                .collect(Collectors.toList());
    }
    
    public boolean hasEditUserPermissions(String graphName, String userKey) {
        Graph graph = graphMetaStorage.getGraphMeta(graphName);
        return hasEditUserPermissions(graph, userKey);
    }
    
    private boolean hasEditUserPermissions(Graph graph, String userKey) {
        return Optional.ofNullable(graph).map(Graph::getUserKeys).map(keys->keys.contains(userKey)).orElse(false);
    }

    public List<VerticesSortCriteriaType> getAvailableSortingCriteria(String graphId) {
        List<String> operationsApplied = graphMetaStorage.getGraphMeta(graphId).getOperationsApplied();
        List<VerticesSortCriteriaType> availableSortingCriteria = new ArrayList(Arrays.asList(
                        VerticesSortCriteriaType.IN_EDGES_COUNT,
                        VerticesSortCriteriaType.OUT_EDGES_COUNT,
                        VerticesSortCriteriaType.VERTEX_TITLE));
        if(operationsApplied != null && operationsApplied.contains(VerticesSortCriteriaType.TOPOLOGICAL.name())) {
            availableSortingCriteria.add(VerticesSortCriteriaType.TOPOLOGICAL);
        }
        return availableSortingCriteria;
    }

    public void addOperationApplied(String graphId, String operationName) {
        Graph graph = graphMetaStorage.getGraphMeta(graphId);
        graph.getOperationsApplied().add(operationName);
        graphMetaStorage.updateGraphMeta(graph);
    }


    public void deleteRogueVertices(String grapheusUserKey, String graphName) {
        vertexStorage.deleteRogue(graphName);
    }

    public Stream<PersistentVertex> getAllVertices(String grapheusUserKey, String graphName) {
        return StreamSupport.stream(vertexStorage.getAllVertices(graphName).spliterator(), false);
    }

    public Stream<PersistentEdge> getAllEdges(String grapheusUserKey, String graphName) {
        return StreamSupport.stream(edgesStorage.getAllEdges(graphName).spliterator(), false);
    }

    public Graph createGraphForUser(String grapheusUserKey, String graphId) throws GraphExistsException {
        Graph g = graphMetaStorage.addGraph(graphId);
        if(g.getUserKeys() == null) {
            g.setUserKeys(new ArrayList<String>());
        }
        g.getUserKeys().add(grapheusUserKey);
        
        g.setPublicAccess(true);
        graphMetaStorage.updateGraphMeta(g);
        return g;
    }

}
