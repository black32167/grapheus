package org.grapheus.web.state;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.grapheus.client.model.graph.edge.EdgeDirection;
import org.grapheus.web.component.vicinity.control.GraphLayout;

import java.io.Serializable;

@RequiredArgsConstructor
public final class VicinityVisualizationConfig implements Serializable {
    public static final String FIELD_SELECTED_VIDS = "selectedVerticesIds";
    private static final long serialVersionUID = 1L;
    public static final String EDGES_DIRECTION = "edgesDirection";
    public static final String DEPTH = "traversalDepth";

    @Getter @Setter
    private GraphLayout layout = GraphLayout.LAYERED;
    @Getter @Setter
    private int propertyHierarchyDepth = 1;

    private final GlobalFilter filter;

    public void setHighlightedProperty(String propertyName) {
        filter.setSelectedPropertyName(propertyName);
    }
    public String getHighlightedProperty() {
        return filter.getSelectedPropertyName();
    }

    public String getSelectedVertexId() {
        return filter.getSelectedVertexId();
    }
    public void setSelectedVertexId(String vertexId) {
        filter.setSelectedVertexId(vertexId);
    }

    public int getTraversalDepth() {
        return filter.getTraversalDepth();
    }
    public void setTraversalDepth(int depth) {
        filter.setTraversalDepth(depth);
    }

    public EdgeDirection getEdgesDirection() {
        return filter.getTraversalDirection();
    }
    public void setEdgesDirection(EdgeDirection direction) {
        filter.setTraversalDirection(direction);
    }

}
