/**
 * 
 */
package grapheus.persistence.storage.graph.transaction.traverse;

import grapheus.persistence.storage.graph.transaction.FoxxEndpointNames;
import grapheus.persistence.storage.graph.transaction.FoxxSupport;
import org.grapheus.client.model.graph.edge.EdgeDirection;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author black
 *
 */
@Service
public class CloneSubgraphTransaction extends FoxxSupport {
    public void generate(String sourceGraph, String newGraphName, String startVertexId, EdgeDirection traversalDirection) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("sourceGraph", sourceGraph);
        parameters.put("newGraphName", newGraphName);
        parameters.put("startVertexId", startVertexId);
        parameters.put("traversalDirection", traversalDirection.name());
        invokeFoxx(FoxxEndpointNames.CLONE_SUBGRAPH, parameters, String.class);
    }
}
