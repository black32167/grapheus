/**
 * 
 */
package grapheus.graph.bulk;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import grapheus.persistence.model.graph.PersistentEdge;
import grapheus.persistence.storage.graph.EdgeStorage;
import lombok.RequiredArgsConstructor;

/**
 * @author black
 */
@Service
@RequiredArgsConstructor(onConstructor = @__({ @Inject }))
public class EdgeBulkImporterFactory {
    private int edgeBulkImportBufferSize = 100;//TODO: parameterize
    
    public interface EdgeBulkImporter extends AutoCloseable {
        void importEdge(PersistentEdge edge);
    }
    private final EdgeStorage edgeStorage;
    
    public EdgeBulkImporter newEdgeImporter(String grapheusUserKey, String graphId) {
        return new EdgeBulkImporter() {
            private Collection<PersistentEdge> edgesBuffer = new ArrayList<PersistentEdge>(edgeBulkImportBufferSize);

            @Override
            synchronized public void close() throws Exception {
                flush();
            }

            @Override
            synchronized public void importEdge(PersistentEdge edge) {
                edgesBuffer.add(edge);
                if(edgesBuffer.size() >= edgeBulkImportBufferSize) {
                    flush();
                }
            }
            
            private void flush() {
                edgeStorage.bulkConnect(graphId, edgesBuffer);
                edgesBuffer.clear();
            }
        };
        
    }
}
