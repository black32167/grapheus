/**
 * 
 */
package org.grapheus.client.model.graph.generate;

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
public class RConnectionRequest {
    private String graphId; 
    private List<String> fromVerticesIds;
    private List<String> toVerticesIds;
}
