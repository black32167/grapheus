/**
 * 
 */
package grapheus.persistence.storage.graph.query.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.grapheus.client.model.graph.edge.EdgeDirection;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import grapheus.persistence.StorageSupport;
import grapheus.persistence.model.graph.PersistentEdge;
import grapheus.persistence.storage.graph.GraphNameUtils;
import grapheus.persistence.storage.graph.query.EdgesFinder;
import grapheus.persistence.storage.traverse.Edge;

/**
 * @author black
 *
 */
@Service
@Slf4j
public class DefaultEdgesFinder extends StorageSupport implements EdgesFinder {

    @Override
    public Collection<PersistentEdge> getNeighbors(String graphName, String artifactId, EdgeDirection edgesDirection, int hops) {
        String vertexColectionName = GraphNameUtils.verticesCollectionName(graphName);
        String directionClause = edgesDirection.name();
        String aql = "FOR v,e in 1.." + hops + " " + directionClause + " '" + vertexColectionName + "/" + artifactId
                + "' " + "GRAPH '" + graphName + "'"
                + "  RETURN e";
        log.debug("getNeighbors query={}", aql);
        List<PersistentEdge> neighborhoodEdges = q(aql, Collections.emptyMap(), PersistentEdge.class).asListRemaining();
        return neighborhoodEdges;
    }
    
    
    @Override
    public List<Edge> outbound(String graphName, List<String> rootVertexKeys, int depth) {
        String vertexColl = GraphNameUtils.verticesCollectionName(graphName);
        String aql =
                "FOR a IN " + vertexColl +
                    " FILTER a._key IN @vKeys " +
                        " FOR v, e IN 1.." + depth + " OUTBOUND a._id GRAPH '" + graphName + "' " +
                            " RETURN DISTINCT {from:e._from, to:e._to}";
        return query(db->db.query(aql, Collections.singletonMap("vKeys", rootVertexKeys), null, Edge.class)).//
                asListRemaining().stream().//
                map(e->{return Edge.builder().from(id2Key(e.getFrom())).to(id2Key(e.getTo())).build();}).//
                collect(Collectors.toList());
    }

    private String id2Key(String id) {
        int idx = id.indexOf("/");
        return idx == -1 ? id : id.substring(idx+1);
        
    }

    @Override
    public void iterateEdges(String graphName, Consumer<PersistentEdge> edgesConsumer) {
        String edgesCollection = GraphNameUtils.edgesCollectionName(graphName);
        String aql = "FOR e IN " + edgesCollection + " RETURN e";
        q(aql, Collections.emptyMap(), PersistentEdge.class).forEachRemaining(edgesConsumer);
    }
    

}
