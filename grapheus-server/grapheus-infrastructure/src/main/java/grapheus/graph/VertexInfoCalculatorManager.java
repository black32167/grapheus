/**
 * 
 */
package grapheus.graph;

import java.util.List;

import javax.inject.Inject;

import org.grapheus.client.model.graph.VertexInfoType;
import org.grapheus.client.model.graph.edge.EdgeDirection;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import grapheus.persistence.graph.calculate.AdjacentEdgesCountCalculator;
import grapheus.persistence.graph.calculate.CalculatedVertexInfo;


/**
 * @author black
 */
@Service
@RequiredArgsConstructor(onConstructor = @__({ @Inject }))
public class VertexInfoCalculatorManager {
    private final AdjacentEdgesCountCalculator adjacentEdgesCountCalculator;
    
    public List<CalculatedVertexInfo> verticesInfo(String userKey, String graphName, VertexInfoType infoType, List<String> ids) {
        switch(infoType) {
        case INBOUND_EDGES:
            return adjacentEdgesCountCalculator.calculate(graphName, EdgeDirection.INBOUND, ids);
        case OUTBOUND_EDGES:
            return adjacentEdgesCountCalculator.calculate(graphName, EdgeDirection.OUTBOUND, ids);
        }
        throw new UnsupportedOperationException("Unsupported vertex info type: " + infoType);
    }
}
