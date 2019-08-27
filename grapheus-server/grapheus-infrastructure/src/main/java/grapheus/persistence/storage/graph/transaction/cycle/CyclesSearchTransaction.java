/**
 * 
 */
package grapheus.persistence.storage.graph.transaction.cycle;

import grapheus.persistence.storage.graph.transaction.FoxxEndpointNames;
import grapheus.persistence.storage.graph.transaction.FoxxSupport;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author black
 *
 */
@Service
public class CyclesSearchTransaction extends FoxxSupport {
    public static class CyclesResult {
        List<List<String>> cycles;
    }
    public List<List<String>> cycles(String graphId) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("graphId", graphId);

        CyclesResult cyclesResult = invokeFoxx(FoxxEndpointNames.FIND_CYCLES, parameters, CyclesResult.class);
        return cyclesResult.cycles;
    }
}
