/**
 * 
 */
package grapheus.persistence.graph.generate;

import javax.inject.Inject;

import org.grapheus.client.model.graph.GraphNamesConstants;
import org.springframework.stereotype.Service;

import grapheus.event.DatabaseInitializedListener;
import grapheus.graph.GraphsManager;
import grapheus.persistence.exception.GraphExistsException;
import grapheus.persistence.model.graph.Graph;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author black
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class EmptyGraphGenerator implements DatabaseInitializedListener {
    
    private final GraphsManager graphsManager;
    
    public Graph createGraph(String grapheusUserKey, String graphId) throws GraphExistsException {
        Graph g = graphsManager.createGraphForUser(grapheusUserKey, graphId);
        return g;
    }
    
    @Override
    public void databaseInitialized() {
        log.info("Database is initialized - creating default graph '{}'", GraphNamesConstants.DEFAULT_GRAPH_NAME);
      
        try {
            createGraph(null, GraphNamesConstants.DEFAULT_GRAPH_NAME);
            log.info("Graph '{}' is created", GraphNamesConstants.DEFAULT_GRAPH_NAME);
        } catch (GraphExistsException e) {
            log.info("Graph '{}' already exists", GraphNamesConstants.DEFAULT_GRAPH_NAME);
        }
    }

}
