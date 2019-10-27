package org.grapheus.web.model.loader;

import lombok.RequiredArgsConstructor;
import org.grapheus.web.component.vicinity.WebGraphUtils;
import org.grapheus.web.model.VicinityGraph;
import org.grapheus.web.state.RepresentationState;
import org.grapheus.web.state.VicinityState;

import java.util.Collections;

@RequiredArgsConstructor
public class VicinityGraphLoader {
    private final RepresentationState representationState;

    public VicinityGraph load() {
        String graphId = representationState.getGraphId();

        if(graphId == null) {
            return VicinityGraph.builder()
                    .vertices(Collections.emptyList())
                    .edges(Collections.emptyList())
                    .build();
        }

        VicinityState vicinityState = representationState.getVicinityState();
        return WebGraphUtils.listNeighbors(
                graphId,
                representationState.getClickedVertexId(),
                vicinityState.getDepth(),
                vicinityState.getEdgesDirection());
    }
}
