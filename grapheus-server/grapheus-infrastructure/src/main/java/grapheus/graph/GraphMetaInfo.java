/**
 * 
 */
package grapheus.graph;

import lombok.Builder;
import lombok.Data;

/**
 * @author black
 *
 */
@Data
@Builder
public class GraphMetaInfo {
    private String name;
    private boolean hasEditPermissions;
}
