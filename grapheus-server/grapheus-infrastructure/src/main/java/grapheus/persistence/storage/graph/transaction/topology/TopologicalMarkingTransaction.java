/**
 * 
 */
package grapheus.persistence.storage.graph.transaction.topology;

import grapheus.persistence.storage.graph.transaction.FoxxEndpointNames;
import grapheus.persistence.storage.graph.transaction.FoxxSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author black
 *
 */
@Service
@Slf4j
public class TopologicalMarkingTransaction extends FoxxSupport {
    public static class TopologicalSortResult {
        boolean cycleFound;
    }

    /**
     * Applies order to nodes in topological order.
     */
    public boolean topologicalOrder(String graphId) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("graphId", graphId);

        TopologicalSortResult result = invokeFoxx(FoxxEndpointNames.TOPOLOGICAL_MARK, parameters, TopologicalSortResult.class);
        return result.cycleFound;
    }
}
