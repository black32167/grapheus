/**
 * 
 */
package grapheus.service.uds;

import lombok.Builder;
import lombok.Value;
import lombok.experimental.Wither;
import org.grapheus.client.model.graph.edge.RAdjacentEdgesFilter;
import org.grapheus.client.model.graph.search.RVertexPropertyFilter;

import java.util.Collection;

/**
 * @author black
 */
@Value
@Builder
@Wither
public class ArtifactsFilter {
    private String user;
    private String title;
    private int limit;
//    private int start;
    private boolean sinks;
    private RAdjacentEdgesFilter minimalAdjacentEdgesFilter;
    private RVertexPropertyFilter vertexPropertyFilter;
    private Collection<String> artifactKeys;
}
