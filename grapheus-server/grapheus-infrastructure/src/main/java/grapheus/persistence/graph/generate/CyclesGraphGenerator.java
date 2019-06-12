/**
 * 
 */
package grapheus.persistence.graph.generate;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import grapheus.persistence.exception.DocumentExistsException;
import grapheus.persistence.exception.GraphExistsException;
import grapheus.persistence.model.graph.PersistentVertex;
import grapheus.persistence.storage.graph.EdgeStorage;
import grapheus.persistence.storage.graph.ExternalCompositeId;
import grapheus.persistence.storage.graph.VertexStorage;
import grapheus.persistence.storage.graph.transaction.cycle.CyclesSearchTransaction;

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
    private final EmptyGraphGenerator emptyGraphGenerator;
    private final CyclesSearchTransaction cyclesFinder;
    
    public void generateCyclesGraph(String grapheusUserKey, String sourceGraphName, String newGraphName) throws GraphExistsException {
        log.info("Searching for the cycles...");
        List<List<String>> cycles = cyclesFinder.cycles(sourceGraphName);
        log.info("Found {} cycles", cycles.size());
        
        if(newGraphName == null) {
            newGraphName = sourceGraphName+"_cycles";
        }
        
        emptyGraphGenerator.createGraph(grapheusUserKey, newGraphName);
        
        int cycleIdx = 0;
        
        for(List<String> cycle:cycles) {
            log.info("Processing cycle {}", cycleIdx++);
            
            Iterator<String> cycleIt = cycle.iterator();
            String nextV = cycleIt.next();
            String firstV = nextV;
           
            String nextVKey = nextV.replaceAll(".*/", "");
            
            String newNextVId = ensureVertexCreated(newGraphName, nextVKey);
            
            while(cycleIt.hasNext()) {
                String prevV = cycleIt.next();
                String prevVKey = prevV.replaceAll(".*/", "");
                
                String newPrevVId = ensureVertexCreated(newGraphName, prevVKey);
                
                edgesStorage.connect(newGraphName, newPrevVId, newNextVId);
                
                nextV = prevV;
                newNextVId = newPrevVId;
            }
            if(firstV != nextV) {
                String firstVKey = firstV.replaceAll(".*/", "");
                String newfirstVId = ExternalCompositeId.from(firstVKey);
                edgesStorage.connect(newGraphName, newfirstVId, newNextVId);
            }
        }
        
        log.info("'Cycling' graph is built");
        
    }

    /**
     * Attempts to create vertex.
     * Note that vertex can participate in multiple cycles so DocumentExistsException must be caught.
     */
    private String ensureVertexCreated(String graphName, String vertexKey) {
        String fullVId = ExternalCompositeId.from(vertexKey);
        try {
            vertexStorage.createVertex(graphName,
                PersistentVertex.builder().//
                    title(vertexKey).//
                    localId(vertexKey).//
                    description("").//
                    externalCompositeId(fullVId).//
                    build());
        } catch (DocumentExistsException e) {
            log.trace("Vertex {} probably participate in multiple cycles", vertexKey);
        }
        return fullVId;
    }
}
