/**
 * 
 */
package grapheus.persistence.storage.graph.transaction.paths;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.arangodb.entity.TraversalEntity;
import com.arangodb.model.TraversalOptions;
import com.arangodb.model.TraversalOptions.Strategy;
import com.arangodb.model.TraversalOptions.UniquenessType;

import grapheus.persistence.StorageSupport;
import grapheus.persistence.model.graph.PersistentEdge;
import grapheus.persistence.model.graph.PersistentVertex;
import grapheus.persistence.storage.graph.GraphNameUtils;

/**
 * @author black
 *
 */
@Service
public class PathsGenerationTransaction2 extends StorageSupport {

    public boolean findPaths(String sourceGraphName, String newGraphName,
            Collection<String> sourceVertexIds,
            Collection<String> targetVertexIds) {
        
        String sourceVerticesCollection = GraphNameUtils.verticesCollectionName(sourceGraphName);
        String sourceEdgesCollection = GraphNameUtils.edgesCollectionName(sourceGraphName);
        String targetVerticesCollection = GraphNameUtils.verticesCollectionName(newGraphName);
        String targetEdgesCollection = GraphNameUtils.edgesCollectionName(newGraphName);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("graphId", sourceGraphName);
        parameters.put("newGraphId", newGraphName);
        parameters.put("sourceVerticesIds", sourceVertexIds);
        parameters.put("targetVerticesIds", targetVertexIds);

        TraversalEntity<PersistentVertex, PersistentEdge> t = query(db->db.executeTraversal(PersistentVertex.class, PersistentEdge.class,
                new TraversalOptions()
                    .strategy(Strategy.breadthfirst)
                    .startVertex(sourceVertexIds.iterator().next())
                    .edgeCollection(sourceEdgesCollection)
//                    .filter("function (config, vertex, path) { "
//                            + " if(vertex._key == }").
                    .verticesUniqueness(UniquenessType.path))
                );
    
        return true;
    }
}
