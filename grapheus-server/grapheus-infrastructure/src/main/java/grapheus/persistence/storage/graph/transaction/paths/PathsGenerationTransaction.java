/**
 * 
 */
package grapheus.persistence.storage.graph.transaction.paths;

import grapheus.persistence.storage.graph.transaction.FoxxEndpointNames;
import grapheus.persistence.storage.graph.transaction.FoxxSupport;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author black
 */
@Service
public class PathsGenerationTransaction extends FoxxSupport {
    public boolean findPaths(String sourceGraphName, String newGraphName,
            Collection<String> boundaryVerticesIds) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("graphId", sourceGraphName);
        parameters.put("newGraphId", newGraphName);
        parameters.put("boundaryVerticesIds",  String.join(",", boundaryVerticesIds));

        invokeFoxx(FoxxEndpointNames.FIND_PATHS, parameters, String.class);
        return true;
    }
}
