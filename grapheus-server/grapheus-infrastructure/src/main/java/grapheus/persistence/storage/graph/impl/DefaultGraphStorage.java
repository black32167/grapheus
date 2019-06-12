/**
 * 
 */
package grapheus.persistence.storage.graph.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Repository;

import com.arangodb.ArangoDBException;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.EdgeDefinition;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import grapheus.persistence.StorageSupport;
import grapheus.persistence.exception.DocumentExistsException;
import grapheus.persistence.exception.DocumentNotFoundException;
import grapheus.persistence.exception.DocumentsConflictException;
import grapheus.persistence.exception.GraphExistsException;
import grapheus.persistence.model.ModelMeta;
import grapheus.persistence.model.graph.Graph;
import grapheus.persistence.model.graph.PersistentVertex;
import grapheus.persistence.storage.graph.GraphNameUtils;
import grapheus.persistence.storage.graph.GraphStorage;

/**
 * @author black
 *
 */
@Slf4j
@Repository
//@RequiredArgsConstructor(onConstructor = @__({ @Inject }))
public class DefaultGraphStorage extends StorageSupport implements GraphStorage {
    private final static long UNPROCESSED_FLUSH_PERIOD_MS = 500;
    private final Set<String> unprocessedGraphNames = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Thread graphUnprocessedStateUpdateThread = new Thread(this::flushUnprocessedStatesCycle, "Flush-Graph-Unprocessed-States");
    
    @PostConstruct
    void init() {
        graphUnprocessedStateUpdateThread.setDaemon(true);
        graphUnprocessedStateUpdateThread.start();
    }
    
    
    @Override
    public void onConnected(ArangoDatabase db) {
        createCollection(db, Graph.class);
    }

    @Override
    public Graph addGraph(String graphName) throws GraphExistsException {
        // Create meta information
        try {
            createDocument(Graph.builder().name(graphName).build());
        } catch (DocumentExistsException e) {
            throw new GraphExistsException(graphName);
        }
        
        // Create graph itself and vertex collection
        String verticesCollection = GraphNameUtils.verticesCollectionName(graphName);
        String edgesCollection = GraphNameUtils.edgesCollectionName(graphName);
        try {
            update(db -> {
                createCollection(db, PersistentVertex.class, verticesCollection);

                db.createGraph(graphName, Collections.singletonList(new EdgeDefinition().//
                    collection(edgesCollection).//
                    from(verticesCollection).//
                    to(verticesCollection)));
            });
        } catch (ArangoDBException e) {
            if (e.getErrorNum() != null && e.getErrorNum() == 1925) {
                log.info("Graph already exists:{}", graphName);
            } else {
                throw e;
            }
        }
        
        return getGraphMeta(graphName);
    }

    @Override
    public List<Graph> getUserGraphs(@NonNull String userKey) {
        String aql = 
                "FOR g IN " + ModelMeta.getCollectionName(Graph.class) + 
                "  FILTER @userKey IN g." + Graph.FIELD_USER_KEYS + " OR g." + Graph.FIELD_PUBLIC +
                "  RETURN g";
        
        return q(aql, Collections.singletonMap("userKey", userKey), Graph.class).asListRemaining();
    }
    
    @Override
    public Graph getGraphMeta(String graphName) {
        Graph graph = findDocument(Graph.class, graphName).orElseThrow(() -> new DocumentNotFoundException("Cannot find graph '" + graphName + "'"));
        if(graph.getOperationsApplied() == null) {
            graph.setOperationsApplied(new ArrayList<>());
        }
        return graph;
    }

    @Override
    public void updateGraphMeta(Graph graph) {
        updateDocument(graph);
    }

    @Override
    public Optional<Graph> getUnprocessedGraph() {
        String aql = 
                "FOR g IN " + ModelMeta.getCollectionName(Graph.class) + 
                "  FILTER g." + Graph.FIELD_PROCESSED_TIMESTAMP + " == null LIMIT 1" +
                "  RETURN g";
        return Optional.ofNullable(q(aql, Collections.emptyMap(), Graph.class).first());
    }

    private void flushUnprocessedStatesCycle() {
        while (true) { // This is a daemon thread
            Iterator<String> it = unprocessedGraphNames.iterator();
            
            while(it.hasNext()) {
                String graphName = it.next();
                
                findDocument(Graph.class, graphName).//
                    filter(g->g.getProcessedTimestamp() != null).//
                    ifPresent(updatingGraph -> {
                        updatingGraph.setProcessedTimestamp(null);
                        try {
                            updateGraphMeta(updatingGraph);
                            it.remove();
                        } catch (DocumentsConflictException e) {
                            // Nothing to do here?
                        }
                    });
            }
            try {
                Thread.sleep(UNPROCESSED_FLUSH_PERIOD_MS);
            } catch (InterruptedException e) {}
        }
    }
    
    @Override
    public void setUnprocessed(String graphName) {
        unprocessedGraphNames.add(graphName); 
    }


    @Override
    public void delete(String graphName) {
        deleteDocument(Graph.class, graphName);
    }

}
