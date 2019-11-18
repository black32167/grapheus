/**
 * 
 */
package org.grapheus.client.model.graph;

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
public class RGraph {
    private String graphId;
    private boolean editPermitted;
    private String generativeGraphId;
    private String generativeProperty;
}
