/**
 * 
 */
package grapheus.persistence.storage.graph.transaction.merge;

import grapheus.persistence.storage.graph.transaction.FoxxEndpointNames;
import grapheus.persistence.storage.graph.transaction.FoxxSupport;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author black
 *
 */
@Service
public class MergeVerticesTransaction extends FoxxSupport {
    public static class MergeResult {
        String newVertexKey;
    }
    public String merge(String grapheusUserKey, String graphId, String newVertexName, Collection<String> verticesIds) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("graphId", graphId);
        parameters.put("newVertexName", newVertexName);
        parameters.put("verticesIds", String.join(",", verticesIds));

        MergeResult mergeResult = invokeFoxx(FoxxEndpointNames.MERGE_VERTICES, parameters, MergeResult.class);
        return mergeResult.newVertexKey;
    }
}
