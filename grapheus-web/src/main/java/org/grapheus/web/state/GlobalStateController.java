package org.grapheus.web.state;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.grapheus.client.model.graph.RGraph;
import org.grapheus.client.model.graph.vertex.RVertex;
import org.grapheus.web.RemoteUtil;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Predicate;

import static org.grapheus.web.page.vertices.list.VerticesPage.PARAM_FILTER_PROPERTY;
import static org.grapheus.web.page.vertices.list.VerticesPage.PARAM_SELECTED_GRAPH;

@RequiredArgsConstructor
public class GlobalStateController implements Serializable {
    @Getter
    private final SharedModels sharedModels;
    @Getter
    private final GlobalFilter filter;

    public void setGraphId(String graphId) {
        RGraph currentGraph = RemoteUtil.graphsAPI().getGraph(graphId);

        if(currentGraph == null) {
            currentGraph = sharedModels.getAvailableGraphs().stream().findFirst().orElse(null);
        }

        filter.setGraphId(currentGraph != null ? currentGraph.getGraphId() : null);
        actualizeSelectedVertexValue();
    }

    public void actualizeSelectedVertexValue() {
        String propertyName = filter.getSelectedPropertyName();
        String propertyValue = filter.getSelectedPropertyValue();
        boolean currentPropertyExists = findProperty(p ->
                propertyName.equals(p.getName()) &&
                        propertyValue.equals(p.getValue()))
                .isPresent();
        if(!currentPropertyExists) {
            RVertex.RProperty newProperty = findProperty(p -> propertyName.equals(p.getName()))
                    .orElseGet(()->findProperty(p->true).orElse(null));
            if(newProperty != null) {
                filter.setSelectedPropertyName(newProperty.getName());
                filter.setSelectedPropertyValue(newProperty.getValue());
            } else {
                filter.setSelectedPropertyName(null);
                filter.setSelectedPropertyValue(null);
            }
        }
    }

    private Optional<RVertex.RProperty> findProperty(Predicate<RVertex.RProperty> filter) {
        RVertex selectedVertex = sharedModels.getActiveVertex();
        if(selectedVertex == null) {
            return Optional.empty();
        }
        return selectedVertex.getProperties()
                .stream()
                .filter(filter)
                .findFirst();
    }

    public void removeSelectedVertices() {
        RemoteUtil.operationAPI().deleteVertices(filter.getGraphId(), filter.getSelectedVerticesIds());

        String selectedVertexId = filter.getSelectedVertexId();
        if(selectedVertexId != null && filter.getSelectedVerticesIds().contains(selectedVertexId)) {
            filter.setSelectedVertexId(null);
        }
        filter.getSelectedVerticesIds().clear();
    }

    public void init(PageParameters pageParameters) {
        String graphId = pageParameters.get(PARAM_SELECTED_GRAPH).toString();
        String selectedProperty = pageParameters.get(PARAM_FILTER_PROPERTY).toString();

        filter.setListPropertyFilterMode(GlobalFilter.PropertyFilterMode.PREFIX);
        filter.setSelectedPropertyName(selectedProperty);
        setGraphId(graphId);
    }
}
