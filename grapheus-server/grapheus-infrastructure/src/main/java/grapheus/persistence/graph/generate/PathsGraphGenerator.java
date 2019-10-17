/**
 * 
 */
package grapheus.persistence.graph.generate;

import grapheus.graph.GraphsManager;
import grapheus.persistence.exception.GraphExistsException;
import grapheus.persistence.storage.graph.transaction.paths.PathsGenerationTransaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Collection;

/**
 * @author black
 *
 */
@Service
@RequiredArgsConstructor(onConstructor = @__({ @Inject }))
@Slf4j
public class PathsGraphGenerator {
    private final PathsGenerationTransaction pathsGenerationTransaction;
    private final GraphsManager graphsManager;
    
    public void generate(String grapheusUserKey, String sourceGraphId, String newGraphId,
            Collection<String> boundaryVerticesIds) {
        try {
            graphsManager.createGraphForUser(grapheusUserKey, newGraphId, sourceGraphId);
        } catch (GraphExistsException e) {
            log.info("Graph '{}' already exists", newGraphId);
        }
        pathsGenerationTransaction.findPaths(sourceGraphId, newGraphId, boundaryVerticesIds);
    }
}
