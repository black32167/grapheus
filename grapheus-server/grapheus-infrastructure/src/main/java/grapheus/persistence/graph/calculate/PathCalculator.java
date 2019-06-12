/**
 * 
 */
package grapheus.persistence.graph.calculate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import grapheus.persistence.StorageSupport;
import grapheus.utils.EntityIdUtils;

/**
 * @author black
 *
 */
@Service
public class PathCalculator extends StorageSupport {
    public List<String> findPath(String graphId, String fromVertexKey, String toVertexKey) {
        String aql = "FOR v IN OUTBOUND SHORTEST_PATH @fromVertexId TO @toVertexId GRAPH @graphId RETURN v._key";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fromVertexId", EntityIdUtils.toId(graphId, fromVertexKey));
        parameters.put("toVertexId", EntityIdUtils.toId(graphId, toVertexKey));
        parameters.put("graphId", graphId);
        
        List<String> path = q(aql, parameters, String.class).asListRemaining();
      
        return path;
        
    }
}
