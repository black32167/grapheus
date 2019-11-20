package org.grapheus.web.model.loader;

import org.grapheus.client.model.graph.RGraph;
import org.grapheus.web.model.GraphView;

import java.util.List;
import java.util.stream.Collectors;

public final class GraphViewFactory {
    public static List<GraphView> createViews(List<RGraph> externalGraphs) {
        return externalGraphs
                .stream()
                .map(rg -> GraphView.builder()
                        .graphId(rg.getGraphId())
                        .editPermitted(rg.isEditPermitted())
                        .generativeGraphId(rg.getGenerativeGraphId())
                        .generativeGraphProperty(rg.getGenerativeProperty())
                        .build())
                .collect(Collectors.toList());
    }

    private GraphViewFactory() {}
}
