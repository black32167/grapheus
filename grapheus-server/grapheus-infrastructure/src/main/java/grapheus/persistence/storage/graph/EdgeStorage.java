package grapheus.persistence.storage.graph;

import java.util.Collection;

import grapheus.persistence.model.graph.PersistentEdge;

public interface EdgeStorage {
    void connectUnchecked(String graphName, String vertex1Id, String vertex2Id);
    void connect(String graphName, String vertex1Id, String vertex2Id);
    void bulkConnect(String graphName, Collection<PersistentEdge> connections);
    void disconnect(String graphId, String fromVertexId, String toVertexId);
    Iterable<PersistentEdge> getAllEdges(String graphName);
}
