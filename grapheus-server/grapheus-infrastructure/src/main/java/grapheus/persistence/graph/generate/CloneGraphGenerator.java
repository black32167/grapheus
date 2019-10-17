/**
 * 
 */
package grapheus.persistence.graph.generate;

import grapheus.graph.GraphsManager;
import grapheus.persistence.exception.GraphExistsException;
import grapheus.persistence.storage.graph.transaction.clone.CloneGraphTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * @author black
 *
 */
@Service
@RequiredArgsConstructor(onConstructor = @__({ @Inject }))
public class CloneGraphGenerator {
    private final CloneGraphTransaction cloneGraphTransaction;
    private final GraphsManager graphsManager;
    
    public void generate(String grapheusUserKey, String sourceGraph, String newGraphName) throws GraphExistsException {
        graphsManager.createGraphForUser(grapheusUserKey, newGraphName, sourceGraph);
        cloneGraphTransaction.generate(sourceGraph, newGraphName);
    }

}
