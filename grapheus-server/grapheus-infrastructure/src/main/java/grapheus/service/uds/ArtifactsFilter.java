/**
 * 
 */
package grapheus.service.uds;

import java.util.Collection;

import org.grapheus.client.model.graph.edge.RAdjacentEdgesFilter;

import lombok.Builder;
import lombok.Value;
import lombok.experimental.Wither;

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
    private Collection<String> artifactKeys;
}
