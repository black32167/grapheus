/**
 * 
 */
package grapheus.persistence.storage.graph.transaction.clone;

import grapheus.persistence.storage.graph.transaction.FoxxSupport;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author black
 *
 */
@Service
public class CloneGraphTransaction extends FoxxSupport {

    public void generate(String sourceGraph, String newGraphName) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("sourceGraphId", sourceGraph);
        parameters.put("newGraphId", newGraphName);

        invokeFoxx("clone", parameters, String.class);
    }

}
