/**
 * 
 */
package grapheus.persistence.storage.graph.transaction.bridge;

import grapheus.persistence.storage.graph.transaction.FoxxEndpointNames;
import grapheus.persistence.storage.graph.transaction.FoxxSupport;
import grapheus.persistence.storage.traverse.Bridges;
import grapheus.persistence.storage.traverse.Edge;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author black
 *
 */
@Service
public class BridgesSearchTransaction extends FoxxSupport {
    public List<Edge> bridges(String graphId) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("graphId", graphId);

        Bridges bridges =  invokeFoxx(FoxxEndpointNames.FIND_BRIDGES, parameters, Bridges.class);
        return Optional.ofNullable(bridges.getBridges()).orElse(Collections.emptyList());
    }
}
