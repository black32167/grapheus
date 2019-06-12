/**
 * 
 */
package grapheus.persistence.storage.graph.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDBException;
import com.arangodb.model.DocumentImportOptions;
import com.arangodb.model.DocumentImportOptions.OnDuplicate;

import grapheus.persistence.StorageSupport;
import grapheus.persistence.model.graph.PersistentEdge;
import grapheus.persistence.storage.graph.EdgeStorage;
import grapheus.persistence.storage.graph.GraphNameUtils;
import grapheus.utils.EntityIdUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @author black
 */
@Repository
@Slf4j
//@RequiredArgsConstructor(onConstructor = @__({ @Inject }))
public class DefaultEdgeStorage extends StorageSupport implements EdgeStorage {

    @Override
    public void connect(String graphName, String vertex1Id, String vertex2Id) {
        if (vertex1Id == null || vertex2Id == null || Objects.equals(vertex1Id, vertex2Id)) {
            return;
        }
        log.info("Connecting '{}'->'{}'", vertex1Id, vertex2Id);
        String vertexCollectionName = GraphNameUtils.verticesCollectionName(graphName);
        String edgeCollectionName = GraphNameUtils.edgesCollectionName(graphName);

        Optional<PersistentEdge> maybeConnection = findConnection(graphName, vertex1Id, vertex2Id);
        if (maybeConnection.isPresent()) {
            // TODO: update 
//            PersistentEdge connection = maybeConnection.get();
//            update(db -> db.graph(graphName).//
//                    edgeCollection(edgeCollectionName).//
//                    updateEdge(connection.getKey(), connection));
        } else {
            // Create new edge
            update(db -> db.graph(graphName).//
                    edgeCollection(edgeCollectionName).//
                    insertEdge(PersistentEdge.builder().//
                            from(vertexCollectionName + "/" + vertex1Id).//
                            to(vertexCollectionName + "/" + vertex2Id).//
                            build()));
        }
    }


    private Optional<PersistentEdge> findConnection(String graphName, String externalCompositeId1,
            String externalCompositeId2) {
        String vertexCollectionName = GraphNameUtils.verticesCollectionName(graphName);
        String edgeCollectionName = GraphNameUtils.edgesCollectionName(graphName);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("a1Id", vertexCollectionName + "/" + externalCompositeId1);
        parameters.put("a2Id", vertexCollectionName + "/" + externalCompositeId2);

        String aql = "FOR e IN " + edgeCollectionName + " FILTER e." + PersistentEdge.FIELD_FROM
                + " == @a1Id AND " + " e." + PersistentEdge.FIELD_TO + " == @a2Id RETURN e ";
        try {
            ArangoCursor<PersistentEdge> edgeCursor = q(aql, parameters, PersistentEdge.class);

            return edgeCursor.hasNext() ? Optional.of(edgeCursor.next()) : Optional.empty();
        } catch (ArangoDBException e) {
            if (e.getErrorNum() == 1203) {// TODO: replace with constant
                return Optional.empty();
            }
            throw e;
        }
    }

    @Override
    public void bulkConnect(String graphName, Collection<PersistentEdge> connections) {
        String edgeCollectionName = GraphNameUtils.edgesCollectionName(graphName);
        update(db -> db.collection(edgeCollectionName).importDocuments(connections, new DocumentImportOptions().onDuplicate(OnDuplicate.update)));
    }

    @Override
    public void disconnect(String graphId, String fromVertexId, String toVertexId) {
        String edgeCollectionName = GraphNameUtils.edgesCollectionName(graphId);
        String aql = "FOR e IN " + edgeCollectionName + " FILTER " +
                "(e." + PersistentEdge.FIELD_FROM + " == @fromVKey) AND " +
                "(e." + PersistentEdge.FIELD_TO + " == @toVKey) " +
                "REMOVE e IN " + edgeCollectionName;
        Map<String, Object> qParams = new HashMap<>();
        qParams.put("fromVKey", EntityIdUtils.toId(graphId, fromVertexId));
        qParams.put("toVKey", EntityIdUtils.toId(graphId, toVertexId));
        q(aql, qParams, Void.class);
    }


    @Override
    public Iterable<PersistentEdge> getAllEdges(String graphName) {
        return q("FOR e IN " +
                GraphNameUtils.edgesCollectionName(graphName) +
                " RETURN e",
                Collections.emptyMap(),
                PersistentEdge.class);
    }


}
