/**
 * 
 */
package grapheus.persistence.storage.graph.impl;

import com.arangodb.ArangoDBException;
import grapheus.TimeService;
import grapheus.persistence.ArangoDBExceptionUtil;
import grapheus.persistence.StorageSupport;
import grapheus.persistence.exception.DocumentNotFoundException;
import grapheus.persistence.model.graph.PersistentVertex;
import grapheus.persistence.query.QueryUtil;
import grapheus.persistence.storage.graph.GraphNameUtils;
import grapheus.persistence.storage.graph.VertexStorage;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author black
 */
@Repository
@Slf4j
@RequiredArgsConstructor(onConstructor = @__({ @Inject }))
public class DefaultVertexStorage extends StorageSupport implements VertexStorage {

    private static final int UNPROCESSED_ITEMS_LIMIT = 100;

    private final TimeService ts;

    public Optional<PersistentVertex> getById(String graphName, String externalCompositeId) {
        String vertexCollectionName = GraphNameUtils.verticesCollectionName(graphName);
        return findDocument(vertexCollectionName, externalCompositeId, PersistentVertex.class);
    }

    @Override
    public void updateVertex(String graphName, PersistentVertex persistingArtifact) {
        long now = ts.getMills();
        persistingArtifact.setUpdatedTimestamp(now);
        String vertexCollectionName = GraphNameUtils.verticesCollectionName(graphName);
        updateDocument(vertexCollectionName, persistingArtifact);
        log.debug("Updated artifact #" + persistingArtifact.getId());
    }

    @Override
    public void updateVertices(String graphName, Collection<PersistentVertex> vertices) {
        long now = ts.getMills();
        vertices.forEach(a->a.setUpdatedTimestamp(now));
        String vertexCollectionName = GraphNameUtils.verticesCollectionName(graphName);
        updateDocuments(vertexCollectionName, vertices);
        log.debug("Updated {} artifacts in collection {}" + vertices.size(), vertexCollectionName);

    }
    @Override
    public void partiallyUpdateVertex(String graphName, PersistentVertex persistingArtifact) {
        long now = ts.getMills();
        persistingArtifact.setUpdatedTimestamp(now);
        String vertexCollectionName = GraphNameUtils.verticesCollectionName(graphName);
        partiallyUpdateDocument(vertexCollectionName, persistingArtifact);
    }

    @Override
    public void createVertex(String graphName, PersistentVertex persistingArtifact) {
        long now = ts.getMills();
        persistingArtifact.setCreatedTimestamp(now);
        persistingArtifact.setUpdatedTimestamp(now);
        if (persistingArtifact.getTitle() == null) {
            persistingArtifact.setTitle("#" + persistingArtifact.getId());
        }
        String vertexCollectionName = GraphNameUtils.verticesCollectionName(graphName);
        createDocument(vertexCollectionName, persistingArtifact);
        log.debug("Created artifact #" + persistingArtifact.getId());
    }

    /**
     * Finds artifacts with processingStartedTS == null, sets it to now() and
     * returns the artifact.
     * 
     * @return
     */
    @Override
    public List<PersistentVertex> pickNextUnprocessedArtifacts(String graphName, Collection<String> artifactsInFlight) {
        Map<String, Object> parameters = new HashMap<>();

        String artsInflightParr = QueryUtil.arrayParameter("if", artifactsInFlight, parameters);

        String vertexCollectionName = GraphNameUtils.verticesCollectionName(graphName);

        String aql = "FOR a IN " + vertexCollectionName + " " + " FILTER " + " a."
                + PersistentVertex.FIELD_ARTIFACT_PROCESSED + " == null AND " + " a." + PersistentVertex.FIELD_ID
                + " NOT IN " + artsInflightParr + " LIMIT " + UNPROCESSED_ITEMS_LIMIT + " RETURN a";
        List<PersistentVertex> unprocessedArts = query(
                db -> db.query(aql, parameters, null, PersistentVertex.class).asListRemaining());
        return unprocessedArts;
    }

    @Override
    public void deleteVertex(String graphId, @NonNull String vertexId) {
        String vertexCollectionName = GraphNameUtils.verticesCollectionName(graphId);
        try {
            update(db -> db.graph(graphId).//
                    vertexCollection(vertexCollectionName).//
                    deleteVertex(vertexId));
        } catch (ArangoDBException e) {
            if(!ArangoDBExceptionUtil.isDocumentNotFound(e)) {
                throw e;
            }
        }
    }

    @Override
    public void deleteVertices(String graphId, Collection<String> verticesIds) {
        for(String vertexId: verticesIds) {
            deleteVertex(graphId, vertexId);
        }
    }

    @Override
    public int getVerticesCount(String graphName) {
        String vertexCollectionName = GraphNameUtils.verticesCollectionName(graphName);
        try {
            return query(db -> db.collection(vertexCollectionName).count().getCount()).intValue();
        } catch (ArangoDBException e) {
            if(ArangoDBExceptionUtil.isDocumentNotFound(e)) {
                throw new DocumentNotFoundException("Cannot find graph " + graphName, e);
            }
            throw e;
        }

    }

    @Override
    public List<PersistentVertex> get(String graphName, Collection<String> artifactKeys) {
        String vertexCollectionName = GraphNameUtils.verticesCollectionName(graphName);
        Map<String, Object> parameters = new HashMap<>();
        String aIdsArrPara = QueryUtil.arrayParameter("a", artifactKeys, parameters);
        String aql = "FOR a IN " + vertexCollectionName + " FILTER a._key IN " + aIdsArrPara + " RETURN a";
        return q(aql, parameters, PersistentVertex.class).asListRemaining();
    }

    // TODO: Test
    @Override
    public List<String> getAllArtifactsProperties(String graphName) {
        String vertexCollectionName = GraphNameUtils.verticesCollectionName(graphName);
        String aql = "RETURN UNIQUE ( FOR a IN " + vertexCollectionName + " RETURN (" +
                "FOR name IN ATTRIBUTES(a." + PersistentVertex.FIELD_SEMANTIC_FEATURES +
                " RETURN name )))";
        return q(aql, Collections.emptyMap(), List.class).first();
    }

    @Override
    public void deleteGraph(String graphName) {
        String vertexCollectionName = GraphNameUtils.verticesCollectionName(graphName);
        String edgeCollectionName = GraphNameUtils.edgesCollectionName(graphName);
        update(db -> {
            try {
                db.graph(graphName).drop();
            } catch (ArangoDBException e) {
                if(e.getErrorNum() != null && e.getErrorNum() != 1924) {
                    log.error("", e);
                }
            }
            try {
                db.collection(edgeCollectionName).drop();
            } catch (ArangoDBException e) {
                if(e.getErrorNum() != null && e.getErrorNum() != 1924) {
                    log.error("", e);
                }
            }
            
            try {
                db.collection(vertexCollectionName).drop(); 
            } catch (ArangoDBException e) {
                if(e.getErrorNum() != null && e.getErrorNum() != 1924) {
                    log.error("", e);
                }
            }
        });
    }

    @Override
    public void deleteRogue(String graphName) {
        String vertexCollectionName = GraphNameUtils.verticesCollectionName(graphName);
        String edgesCollectionName = GraphNameUtils.edgesCollectionName(graphName);
 
        String deleteAql = 
                  " LET vsIds = (FOR v IN " + vertexCollectionName + " FILTER v.title == null RETURN v._id) "
                + " LET re=(FOR e IN " + edgesCollectionName + " FILTER e._from IN vsIds OR e._to IN vsIds REMOVE e._key IN " + edgesCollectionName + ")"
                + " LET rv=(FOR v IN " + vertexCollectionName + " FILTER v._id IN vsIds REMOVE v._key IN " + vertexCollectionName + ")" 
                + " RETURN 1";
        q(deleteAql, Collections.emptyMap(), Integer.class);
        
    }

    @Override
    public Iterable<PersistentVertex> getAllVertices(String graphName) {
        return q("FOR v IN " +
                GraphNameUtils.verticesCollectionName(graphName) +
                " RETURN v",
                Collections.emptyMap(),
                PersistentVertex.class);
    }


}
