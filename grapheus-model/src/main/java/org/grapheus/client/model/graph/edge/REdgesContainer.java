/**
 * 
 */
package org.grapheus.client.model.graph.edge;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author black
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class REdgesContainer {
    private List<REdge> edges;
    
    
}
