/**
 * 
 */
package grapheus.persistence.storage.graph.transaction.traverse;

import java.util.HashMap;
import java.util.Map;

import org.grapheus.client.model.graph.edge.EdgeDirection;
import org.springframework.stereotype.Service;

import com.arangodb.model.TransactionOptions;

import grapheus.persistence.storage.graph.GraphNameUtils;
import grapheus.persistence.storage.graph.transaction.ServerSideTransaction;

/**
 * @author black
 *
 */
@Service
public class TraversalGraphTransaction extends ServerSideTransaction  {

    public void generate(String sourceGraph, String newGraphName, String startVertexId, EdgeDirection traversalDirection) {
        String targetVCollection = GraphNameUtils.verticesCollectionName(newGraphName);
        String targetECollection = GraphNameUtils.edgesCollectionName(newGraphName);
        Map<String, String> parameters = new HashMap<>();
        parameters.put("sourceGraph", sourceGraph);
        parameters.put("newGraphName", newGraphName);
        parameters.put("startVertexId", startVertexId);
        parameters.put("traversalDirection", traversalDirection.name());
        transaction(
                "TraversalTransaction.js",
                Map.class,
                new TransactionOptions()
                    .writeCollections(targetVCollection,targetECollection)
                    .params(parameters));
    }

}
