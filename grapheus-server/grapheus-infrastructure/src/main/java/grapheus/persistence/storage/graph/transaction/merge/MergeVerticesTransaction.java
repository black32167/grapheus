/**
 * 
 */
package grapheus.persistence.storage.graph.transaction.merge;

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
public class MergeVerticesTransaction extends ServerSideTransaction {

    public String merge(String grapheusUserKey, String graphId, String newVertexName, Collection<String> verticesIds) {
        String verticesCollection = GraphNameUtils.verticesCollectionName(graphId);
        String edgesCollection = GraphNameUtils.edgesCollectionName(graphId);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("graphId", graphId);
        parameters.put("newVertexName", newVertexName);
        parameters.put("verticesIds", verticesIds);
        
        return transaction("MergeVertices.js", String.class,   
                new TransactionOptions().
                    params(parameters).
                    writeCollections(verticesCollection, edgesCollection));
    }

}
