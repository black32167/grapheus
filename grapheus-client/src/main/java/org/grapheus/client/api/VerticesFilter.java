/**
 * 
 */
package org.grapheus.client.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.grapheus.client.model.graph.edge.RAdjacentEdgesFilter;
import org.grapheus.client.model.graph.search.RVertexPropertyFilter;

import java.util.Set;

/**
 * @author black
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerticesFilter {
    private Boolean sinks;
    private String title;
    private RAdjacentEdgesFilter minAdjacentEdgesFilter;
    private RVertexPropertyFilter vertexPropertyFilter;
    private Set<String> verticesIds;
    
    public static class ArtifactsFilterBuilder {
        RAdjacentEdgesFilter minAdjacentEdgesFilter = new RAdjacentEdgesFilter();
    }
}
