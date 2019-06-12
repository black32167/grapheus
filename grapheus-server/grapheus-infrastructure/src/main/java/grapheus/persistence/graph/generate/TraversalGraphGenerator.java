/**
 * 
 */
package grapheus.persistence.graph.generate;

import javax.inject.Inject;

import org.grapheus.client.model.graph.edge.EdgeDirection;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import grapheus.persistence.exception.GraphExistsException;
import grapheus.persistence.storage.graph.transaction.traverse.TraversalGraphTransaction;

/**
 * @author black
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__({ @Inject }))
public class TraversalGraphGenerator {
    private final TraversalGraphTransaction traversalGraphTransaction;
    private final EmptyGraphGenerator emptyGraphGenerator;
    
    public void generate(
            String grapheusUserKey,
            String sourceGraph,
            String newGraphName,
            String startVertexId,
            EdgeDirection traversalDirection) throws GraphExistsException {
        emptyGraphGenerator.createGraph(grapheusUserKey, newGraphName);
        traversalGraphTransaction.generate(sourceGraph, newGraphName, startVertexId, traversalDirection);
    }

}
