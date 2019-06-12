/**
 * 
 */
package grapheus.persistence.storage.graph.transaction.paths;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.arangodb.model.TransactionOptions;

import grapheus.persistence.storage.graph.GraphNameUtils;
import grapheus.persistence.storage.graph.transaction.ServerSideTransaction;

/**
 * @author black
 *
 */
@Service
public class PathsGenerationTransaction extends ServerSideTransaction {

    public boolean findPaths(String sourceGraphName, String newGraphName,
            Collection<String> boundaryVerticesIds) {
        
        String targetVerticesCollection = GraphNameUtils.verticesCollectionName(newGraphName);
        String targetEdgesCollection = GraphNameUtils.edgesCollectionName(newGraphName);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("graphId", sourceGraphName);
        parameters.put("newGraphId", newGraphName);
        parameters.put("boundaryVerticesIds", boundaryVerticesIds);
        
        transaction("FindPaths.js", Boolean.class,   
                new TransactionOptions().
                    params(parameters).
                    writeCollections(targetVerticesCollection, targetEdgesCollection));
        
        return true;
    }
}
