/**
 * 
 */
package org.grapheus.client.model.graph.generate;

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
public class RDisconnectionRequest {
    private String graphId;
    private String fromVertexId;
    private String toVertexId;
}
