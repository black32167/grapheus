/**
 * 
 */
package grapheus.persistence.storage.graph;

import java.util.List;
import java.util.Optional;

import grapheus.persistence.exception.GraphExistsException;
import grapheus.persistence.model.graph.Graph;

public interface GraphStorage {
    Graph addGraph(String graphName) throws GraphExistsException;
    List<Graph> getUserGraphs(String userKey);
    Graph getGraphMeta(String graphName);
    void updateGraphMeta(Graph graph);
    Optional<Graph> getUnprocessedGraph();
    void setUnprocessed(String graphName);
    void delete(String graphName);
}
