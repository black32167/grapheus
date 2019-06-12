/**
 * 
 */
package grapheus.persistence.storage.graph.query;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.grapheus.client.model.graph.VerticesSortCriteria;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import grapheus.persistence.model.graph.PersistentVertex;
import grapheus.service.uds.ArtifactsFilter;

/**
 * @author black
 */
public interface VertexFinder {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class SearchResult {
        private Collection<PersistentVertex> vertices;
        private long totalCount;
    }
    
    SearchResult findVerticesByCriteria(
            String graphName,
            ArtifactsFilter artifactsFilter,
            List<VerticesSortCriteria> verticesSortCriteria);


    List<String> findSinks(String graphName);

    void findVerticesByFeature(
            String graphName, 
            String featureType, Set<String> featureValues, Set<String> excludedArtifactsIds,
            Consumer<PersistentVertex> foundConsumer);

    void iterateAllVertices(String graphName, Consumer<PersistentVertex> verticesConsumer);
}
