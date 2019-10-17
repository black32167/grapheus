/**
 * 
 */
package grapheus.persistence.graph.generate;

import grapheus.graph.GraphsManager;
import grapheus.persistence.exception.DocumentExistsException;
import grapheus.persistence.exception.GraphExistsException;
import grapheus.persistence.storage.graph.EdgeStorage;
import grapheus.persistence.storage.graph.ExternalCompositeId;
import grapheus.persistence.storage.graph.VertexStorage;
import grapheus.persistence.storage.graph.transaction.cycle.CyclesSearchTransaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Iterator;
import java.util.List;

/**
 * @author black
 *
 */
@Service
@RequiredArgsConstructor(onConstructor = @__({ @Inject }))
@Slf4j
public class CyclesGraphGenerator {
    private final EdgeStorage edgesStorage;
    private final VertexStorage vertexStorage;
    private final GraphsManager graphsManager;
    private final CyclesSearchTransaction cyclesFinder;
    
    public void generateCyclesGraph(String grapheusUserKey, String sourceGraphName, String newGraphName) throws GraphExistsException {
        log.info("Searching for the cycles...");
        List<List<String>> cycles = cyclesFinder.cycles(sourceGraphName);
        log.info("Found {} cycles", cycles.size());
        
        if(newGraphName == null) {
            newGraphName = sourceGraphName+"_cycles";
        }
        
        graphsManager.createGraphForUser(grapheusUserKey, newGraphName, sourceGraphName);
        
        int cycleIdx = 0;
        
        for(List<String> cycle:cycles) {
            log.info("Processing cycle {}", cycleIdx++);
            
            Iterator<String> cycleIt = cycle.iterator();
            String nextV = cycleIt.next();
            String firstV = nextV;
           
            String nextVKey = nextV.replaceAll(".*/", "");
            
            String newNextVId = copyVertex(sourceGraphName, newGraphName, nextVKey);
            
            while(cycleIt.hasNext()) {
                String prevV = cycleIt.next();
                String prevVKey = prevV.replaceAll(".*/", "");
                
                String newPrevVId = copyVertex(sourceGraphName, newGraphName, prevVKey);
                
                edgesStorage.connectUnchecked(newGraphName, newPrevVId, newNextVId);
                
                nextV = prevV;
                newNextVId = newPrevVId;
            }
            if(firstV != nextV) {
                String firstVKey = firstV.replaceAll(".*/", "");
                String newfirstVId = ExternalCompositeId.from(firstVKey);
                edgesStorage.connectUnchecked(newGraphName, newfirstVId, newNextVId);
            }
        }
        
        log.info("'Cycling' graph is built");
        
    }

    /**
     * Attempts to create vertex.
     * Note that vertex can participate in multiple cycles so DocumentExistsException must be caught.
     */
    private String copyVertex(String sourceGraphName, String newGraphName, String vertexKey) {
       // String fullVId = ExternalCompositeId.from(vertexKey);
        vertexStorage.getById(sourceGraphName, vertexKey).ifPresent(sourceVertex -> {
            try {
                vertexStorage.createVertex(newGraphName, sourceVertex);
            } catch (DocumentExistsException e) {
                log.trace("Vertex {} probably participate in multiple cycles", vertexKey);
            }
        });

        return vertexKey;
    }
}
