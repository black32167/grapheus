/**
 * 
 */
package grapheus.persistence.storage.graph.query.impl;

import com.arangodb.ArangoCursor;
import grapheus.persistence.StorageSupport;
import grapheus.persistence.model.graph.PersistentVertex;
import grapheus.persistence.query.QueryUtil;
import grapheus.persistence.query.VertexFilterQuery;
import grapheus.persistence.storage.graph.GraphNameUtils;
import grapheus.persistence.storage.graph.query.VertexFinder;
import grapheus.service.uds.ArtifactsFilter;
import grapheus.view.SemanticFeature;
import lombok.extern.slf4j.Slf4j;
import org.grapheus.client.model.graph.VerticesSortCriteria;
import org.grapheus.client.model.graph.edge.EdgeDirection;
import org.grapheus.client.model.graph.edge.RAdjacentEdgesFilter;
import org.grapheus.client.model.graph.search.RVertexPropertyFilter;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author black
 *
 */
@Repository
@Slf4j
public class DefaultVertexFinder extends StorageSupport implements VertexFinder {

    private static final int SINKS_LIMIT = 100;

    @Override
    public List<String> findSinks(String graphName) {
        String vertexColl = GraphNameUtils.verticesCollectionName(graphName);
        String aql =
            "FOR a IN " + vertexColl + 
                " LET edges = (FOR v IN 1..1 OUTBOUND a._id GRAPH '" + graphName + "' RETURN v)" +
                " FILTER LENGTH(edges) == 0 LIMIT "+ SINKS_LIMIT + " " +
            "RETURN a._key";
        
        List<String> artifactIds = query(db->db.query(aql, Collections.emptyMap(), null, String.class)).//
                asListRemaining();
        
        return artifactIds;
    }
    
    @Override
    public SearchResult findVerticesByCriteria(
            String graphName, ArtifactsFilter artifactsFilter, List<VerticesSortCriteria> verticesSortCriteria) {

        VertexFilterQuery query = find(graphName, artifactsFilter, verticesSortCriteria);

        String aql = query.renderAQL();
        log.debug("Query = '{}'", aql);
        ArangoCursor<PersistentVertex> cursor = q(aql, query.parameters(), PersistentVertex.class);
        Collection<PersistentVertex> vertices = cursor.asListRemaining();
        
        VertexFilterQuery countQuery = find(graphName, artifactsFilter.withLimit(-1), verticesSortCriteria).count();
        ArangoCursor<Long> countCursor = q(countQuery.renderAQL(), query.parameters(), Long.class);
        long count = countCursor.first();

        return new SearchResult(vertices, count);
    }
    
    /**
     * Returns vertices by filter.
     */
    private VertexFilterQuery find(String graphName, ArtifactsFilter taskFilter, List<VerticesSortCriteria> verticesSortCriteria) {

        if(taskFilter.isSinks()) {
            taskFilter = taskFilter.withArtifactKeys(findSinks(graphName));
        }
        
        VertexFilterQuery query = new VertexFilterQuery(graphName);

        if(taskFilter.getMinimalAdjacentEdgesFilter() != null) {
            RAdjacentEdgesFilter f = taskFilter.getMinimalAdjacentEdgesFilter();
            query.withEdgesSubquery(f.getDirection()).addCountFilter(f.getAmount());
        }
        
        query.addInFilter("_key", taskFilter.getArtifactKeys());
      
        query.addLikeFilter(PersistentVertex.FIELD_TITLE, taskFilter.getTitle());

        RVertexPropertyFilter vertexPropertyFilter = taskFilter.getVertexPropertyFilter();
        if(vertexPropertyFilter != null) {
            query.addInPropertyFilter(PersistentVertex.FIELD_VIEW_HINT_TYPE, vertexPropertyFilter.getName());
            query.addInPropertyFilter(PersistentVertex.FIELD_VIEW_HINT_VAL, vertexPropertyFilter.getValue());
        }

        query.setLimit(0/*taskFilter.getStart()*/, taskFilter.getLimit());
       
        for(VerticesSortCriteria c: verticesSortCriteria) {
            switch(c.getSortingType()) {
            case OUT_EDGES_COUNT:
                query.withEdgesSubquery(EdgeDirection.OUTBOUND).sortByEdgesCount(c.getSortDirection());
                break;
            case IN_EDGES_COUNT:
                query.withEdgesSubquery(EdgeDirection.INBOUND).sortByEdgesCount(c.getSortDirection());
                break;
            case VERTEX_TITLE:
                query.sortByProperty(PersistentVertex.FIELD_TITLE, c.getSortDirection());
                break;
            case TOPOLOGICAL:
                query.sortByProperty(PersistentVertex.VIRTUAL_ORDER, c.getSortDirection());
            }
        }
        return query;

    }

    @Override
    public void findVerticesByFeature(String graphName, String featureType, Set<String> featureValues,
            Set<String> excludedArtifactsIds, Consumer<PersistentVertex> foundArtifactsCounsumer) {

        if (featureValues.isEmpty()) {
            return;
        }

        Map<String, Object> parametersMap = new HashMap<>();
        parametersMap.put("featureType", featureType);
        
        String vertexCollection = GraphNameUtils.verticesCollectionName(graphName);

        String hintsArrayParam = QueryUtil.arrayParameter("p", featureValues, parametersMap);

        // 'Exclude' artifactId param
        String excludesArtsIdParam = QueryUtil.arrayParameter("e", excludedArtifactsIds, parametersMap);

        // AQL
        String crossReferencesAQL = "FOR a IN " + vertexCollection
                + " FILTER " + " LENGTH(a." + PersistentVertex.FIELD_SEMANTIC_FEATURES + "[* FILTER "
                + " CURRENT." + SemanticFeature.FEATURE + " == @featureType AND " + " CURRENT." + SemanticFeature.VALUE
                + " IN " + hintsArrayParam + "]) > 0 AND " + " a." + PersistentVertex.FIELD_ID + " NOT IN "
                + excludesArtsIdParam + " RETURN a";

        update(db -> db.query(crossReferencesAQL, parametersMap, null, PersistentVertex.class).//
                forEachRemaining(foundArtifactsCounsumer));
    }


    @Override
    public void iterateAllVertices(String graphName, Consumer<PersistentVertex> verticesConsumer) {
        String vertexCollection = GraphNameUtils.verticesCollectionName(graphName);
        String aql = "FOR a IN " + vertexCollection + " RETURN a";
        q(aql, Collections.emptyMap(), PersistentVertex.class).forEachRemaining(verticesConsumer);
        
    }

}
