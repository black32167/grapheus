/**
 * 
 */
package org.grapheus.web.model;

import java.io.Serializable;
import org.apache.wicket.model.LoadableDetachableModel;
import org.grapheus.client.model.graph.edge.EdgeDirection;
import org.grapheus.web.component.shared.SerializableSupplier;
import org.grapheus.web.component.vicinity.WebGraphUtils;
import org.grapheus.web.component.vicinity.control.GraphLayout;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @author black
 */
@RequiredArgsConstructor
public class VicinityModel extends LoadableDetachableModel<VicinityGraph> {
    private static final long serialVersionUID = 1L;
 
    @Getter @Setter
    public final static class Filter implements Serializable {
        private static final long serialVersionUID = 1L;
        public static final String EDGES_DIRECTION = "edgesDirection";
        public static final String DEPTH = "depth";
        public static final String FIELD_SELECTED_VERTEX_ID = "selectedVertexId";
        
        private int depth = 1;
        
        private EdgeDirection edgesDirection = EdgeDirection.ANY;
        
        private String selectedVertexId;
        
        private GraphLayout layout = GraphLayout.LAYERED;
    }
    
    @Getter
    private final Filter filter = new Filter();
    
    private final SerializableSupplier<String> graphIdSupplier;
    
    @Override
    protected VicinityGraph load() {//TODO: return VicinityGraph
        String artifactId = filter.getSelectedVertexId();

        VicinityGraph graphView = WebGraphUtils.listNeighbors(
                graphIdSupplier.get(), 
                artifactId, 
                filter.getDepth(),
                filter.getEdgesDirection());


        return graphView;
    }
}
