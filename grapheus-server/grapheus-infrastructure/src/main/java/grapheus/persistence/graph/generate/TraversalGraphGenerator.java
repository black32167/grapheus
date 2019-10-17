/**
 * 
 */
package grapheus.persistence.graph.generate;

import grapheus.graph.GraphsManager;
import grapheus.persistence.exception.GraphExistsException;
import grapheus.persistence.storage.graph.transaction.traverse.CloneSubgraphTransaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.grapheus.client.model.graph.edge.EdgeDirection;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * @author black
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__({ @Inject }))
public class TraversalGraphGenerator {
    private final CloneSubgraphTransaction cloneSubgraphTransaction;
    private final GraphsManager graphsManager;
    
    public void generate(
            String grapheusUserKey,
            String sourceGraphId,
            String newGraphId,
            String startVertexId,
            EdgeDirection traversalDirection) throws GraphExistsException {
        graphsManager.createGraphForUser(grapheusUserKey, newGraphId, sourceGraphId);
        cloneSubgraphTransaction.generate(sourceGraphId, newGraphId, startVertexId, traversalDirection);
    }

}
