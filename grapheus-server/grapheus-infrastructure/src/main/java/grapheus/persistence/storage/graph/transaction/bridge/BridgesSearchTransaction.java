/**
 * 
 */
package grapheus.persistence.storage.graph.transaction.bridge;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.arangodb.model.TransactionOptions;

import grapheus.persistence.storage.graph.transaction.ServerSideTransaction;
import grapheus.persistence.storage.traverse.Bridges;
import grapheus.persistence.storage.traverse.Edge;

/**
 * @author black
 *
 */
@Service
public class BridgesSearchTransaction extends ServerSideTransaction {

    public List<Edge> bridges(String graphName) {
        Bridges bridges =  transaction("BridgeTransaction.js", Bridges.class, new TransactionOptions().params(graphName));    
        return Optional.ofNullable(bridges.getBridges()).orElse(Collections.emptyList());
    }
    
}
