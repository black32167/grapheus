/**
 * 
 */
package grapheus.graph.bulk;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import grapheus.absorb.VertexPersister;
import grapheus.persistence.model.graph.PersistentVertex;
import lombok.RequiredArgsConstructor;

/**
 * @author black
 */
@Service
@RequiredArgsConstructor(onConstructor = @__({ @Inject }))
public class VertexBulkImporterFactory {
    private int vertexBulkImportBufferSize = 100;//TODO: parameterize
    
    public interface VertexBulkImporter extends AutoCloseable {
        void importVertex(PersistentVertex vertex);
    }
    private final VertexPersister vertexPersister;
    
    public VertexBulkImporter newVertexImporter(String grapheusUserKey, String graphId) {
        return new VertexBulkImporter() {
            private Collection<PersistentVertex> verticesBuffer = new ArrayList<PersistentVertex>(vertexBulkImportBufferSize);

            @Override
            synchronized public void close() throws Exception {
                flush();
            }

            @Override
            synchronized public void importVertex(PersistentVertex vertex) {
                verticesBuffer.add(vertex);
                if(verticesBuffer.size() >= vertexBulkImportBufferSize) {
                    flush();
                }
            }
            
            private void flush() {
                vertexPersister.bulkUpdate(graphId, verticesBuffer);
                verticesBuffer.clear();
            }
        };
        
    }
}
