/**
 * 
 */
package grapheus.persistence.storage.graph.transaction.topology;

import org.springframework.stereotype.Service;

import com.arangodb.model.TransactionOptions;

import lombok.extern.slf4j.Slf4j;
import grapheus.persistence.storage.graph.GraphNameUtils;
import grapheus.persistence.storage.graph.transaction.ServerSideTransaction;

/**
 * @author black
 *
 */
@Service
//@RequiredArgsConstructor(onConstructor = @__({ @Inject }))
@Slf4j
public class TopologicalMarkingTransaction extends ServerSideTransaction {
    /**
     * Applies ascribes order to nodes in topological order.
     * 
     * @param graphName
     * @return true if the graph contains cycles
     */
    public Boolean topologicalOrder(String graphName) {
        String verticesCollection = GraphNameUtils.verticesCollectionName(graphName);
        return transaction("TopologicalSort.js", Boolean.class,
                new TransactionOptions().
                    params(graphName).
                    writeCollections(verticesCollection));
       
    }

}
