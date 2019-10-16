/**
 * 
 */
package grapheus.persistence.graph.generate;

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
    private final EmptyGraphGenerator emptyGraphGenerator;
    
    public void generate(String grapheusUserKey, String sourceGraphName, String newGraphName,
            Collection<String> boundaryVerticesIds) {
        try {
            emptyGraphGenerator.createGraph(grapheusUserKey, newGraphName);
        } catch (GraphExistsException e) {
            log.info("Graph '{}' already exists", newGraphName);
        }
        pathsGenerationTransaction.findPaths(sourceGraphName, newGraphName, boundaryVerticesIds);
    }
}
