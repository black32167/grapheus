/**
 * 
 */
package grapheus.persistence.storage.graph.query;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.grapheus.client.model.graph.edge.EdgeDirection;

import grapheus.persistence.model.graph.PersistentEdge;
import grapheus.persistence.storage.traverse.Edge;

/**
 * @author black
 *
 */
public interface EdgesFinder {
    Collection<Edge> getNeighbors(String graphName, String vertexId, EdgeDirection edgesDirection, int hops);
    List<Edge> outbound(String graphName, List<String> rootVertexKeys, int depth);
    void iterateEdges(String newGraphName, Consumer<PersistentEdge> edgesConsumer);
}
