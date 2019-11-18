/**
 * 
 */
package grapheus.graph;

import grapheus.persistence.model.graph.Graph;
import lombok.Builder;
import lombok.Data;

/**
 * @author black
 *
 */
@Data
@Builder
public class GraphMetaInfo {
    private Graph persistentGraph;
    private boolean hasEditPermissions;

    public String getGraphId() {
        return persistentGraph.getName();
    }

    public String getSourceGraphId() {
        return persistentGraph.getSourceGraphId();
    }

    public String getSourceGraphProperty() {
        return persistentGraph.getGenerativePropertyName();
    }
}
