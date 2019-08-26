/**
 * 
 */
package grapheus.persistence.storage.graph.transaction.merge;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import grapheus.persistence.storage.graph.transaction.FoxxEndpointNames;
import grapheus.persistence.storage.graph.transaction.FoxxSupport;
import org.springframework.stereotype.Service;

import com.arangodb.model.TransactionOptions;

import grapheus.persistence.storage.graph.GraphNameUtils;
import grapheus.persistence.storage.graph.transaction.ServerSideTransaction;

/**
 * @author black
 *
 */
@Service
public class MergeVerticesTransaction extends FoxxSupport {
    public String merge(String grapheusUserKey, String graphId, String newVertexName, Collection<String> verticesIds) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("graphId", graphId);
        parameters.put("newVertexName", newVertexName);
        parameters.put("verticesIds", String.join(",", verticesIds));
        
        return invokeFoxx(FoxxEndpointNames.MERGE_VERTICES, parameters, String.class);
    }
}
