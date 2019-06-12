/**
 * 
 */
package grapheus.persistence.graph.generate;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import grapheus.persistence.exception.GraphExistsException;
import grapheus.persistence.storage.graph.transaction.clone.CloneGraphTransaction;

/**
 * @author black
 *
 */
@Service
@RequiredArgsConstructor(onConstructor = @__({ @Inject }))
public class CloneGraphGenerator {
    private final CloneGraphTransaction cloneGraphTransaction;
    private final EmptyGraphGenerator emptyGraphGenerator;
    
    public void generate(String grapheusUserKey, String sourceGraph, String newGraphName) throws GraphExistsException {
        emptyGraphGenerator.createGraph(grapheusUserKey, newGraphName);
        cloneGraphTransaction.generate(sourceGraph, newGraphName);
    }

}
