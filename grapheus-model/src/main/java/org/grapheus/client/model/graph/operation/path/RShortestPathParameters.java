/**
 * 
 */
package org.grapheus.client.model.graph.operation.path;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author black
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RShortestPathParameters {
    private String graphId;
    private String fromVertexId;
    private String toVertexId;
}
