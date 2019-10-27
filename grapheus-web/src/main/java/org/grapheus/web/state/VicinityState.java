package org.grapheus.web.state;

import lombok.Getter;
import lombok.Setter;
import org.grapheus.client.model.graph.edge.EdgeDirection;
import org.grapheus.web.component.vicinity.control.GraphLayout;

import java.io.Serializable;

@Getter
@Setter
public final class VicinityState implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String EDGES_DIRECTION = "edgesDirection";
    public static final String DEPTH = "depth";

    private int depth = 1;

    private EdgeDirection edgesDirection = EdgeDirection.ANY;

    private GraphLayout layout = GraphLayout.LAYERED;

    private String selectedVerticesTag;
    private String selectedEdgesTag;
}
