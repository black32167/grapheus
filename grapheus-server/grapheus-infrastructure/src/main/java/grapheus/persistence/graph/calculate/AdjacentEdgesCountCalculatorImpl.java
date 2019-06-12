/**
 * 
 */
package grapheus.persistence.graph.calculate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.grapheus.client.model.graph.edge.EdgeDirection;
import org.springframework.stereotype.Repository;

import grapheus.persistence.StorageSupport;
import grapheus.persistence.query.QueryUtil;
import grapheus.persistence.storage.graph.GraphNameUtils;
import grapheus.utils.EntityIdUtils;

/**
 * @author black
 *
 */
@Repository
public class AdjacentEdgesCountCalculatorImpl extends StorageSupport implements AdjacentEdgesCountCalculator {

    @Override
    public List<CalculatedVertexInfo> calculate(String graphName, EdgeDirection edgesDirection, List<String> verticesKeys) {
       
        String aggregatingVertex = getAggregatingVertex("e", edgesDirection);
        String vertexCollectionName = GraphNameUtils.verticesCollectionName(graphName);
        String edgesCollectionName = GraphNameUtils.edgesCollectionName(graphName);
        
        Map<String, Object> parameters = new HashMap<>();
        String artsIdsPar = QueryUtil.arrayParameter("if", EntityIdUtils.toIds(vertexCollectionName, verticesKeys), parameters);
        String aql = "FOR e IN " + edgesCollectionName + 
                " FILTER " + aggregatingVertex + " IN " + artsIdsPar + 
                " COLLECT aId=" + aggregatingVertex + " WITH COUNT INTO eCount" +
                " RETURN {vertexId:aId, intValue:eCount}";
        
        return q(aql, parameters, CalculatedVertexInfo.class).asListRemaining();
        
    }

    private String getAggregatingVertex(String edgeAlias, EdgeDirection edgesDirection) {
        switch(edgesDirection) {
        case INBOUND: return edgeAlias+"._to";
        case OUTBOUND: return edgeAlias+"._from";
        default:
            throw new UnsupportedOperationException("Unsupported direction: " + edgesDirection);
        }
    }

}
