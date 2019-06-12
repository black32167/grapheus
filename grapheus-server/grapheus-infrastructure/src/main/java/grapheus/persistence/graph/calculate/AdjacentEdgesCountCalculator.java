/**
 * 
 */
package grapheus.persistence.graph.calculate;

import java.util.List;

import org.grapheus.client.model.graph.edge.EdgeDirection;

/**
 * @author black
 *
 */
public interface AdjacentEdgesCountCalculator {
    List<CalculatedVertexInfo> calculate(String graphName, EdgeDirection edgesDirection, List<String> verticesKeys);
}
