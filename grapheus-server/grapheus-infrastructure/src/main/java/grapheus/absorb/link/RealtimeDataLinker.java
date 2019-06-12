/**
 * 
 */
package grapheus.absorb.link;

import java.util.Collection;

import grapheus.persistence.model.graph.PersistentEdge;
import grapheus.persistence.model.graph.PersistentVertex;

/**
 * Implementations of this interface are supposed to process
 * @author black
 *
 */
public interface RealtimeDataLinker {
    Collection<PersistentEdge> link(String graphName, PersistentVertex v);
}
