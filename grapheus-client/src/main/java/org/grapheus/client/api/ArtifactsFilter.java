/**
 * 
 */
package org.grapheus.client.api;

import java.util.Set;

import org.grapheus.client.model.graph.edge.RAdjacentEdgesFilter;

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
public class ArtifactsFilter {
    private Boolean sinks;
    private String title;
    private RAdjacentEdgesFilter minAdjacentEdgesFilter;
    private Set<String> verticesIds;
    
    public static class ArtifactsFilterBuilder {
        RAdjacentEdgesFilter minAdjacentEdgesFilter = new RAdjacentEdgesFilter();
    }
}
