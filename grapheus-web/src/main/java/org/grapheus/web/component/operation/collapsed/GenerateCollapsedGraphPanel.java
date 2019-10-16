package org.grapheus.web.component.operation.collapsed;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.grapheus.client.model.graph.vertex.RVertex;
import org.grapheus.web.RemoteUtil;
import org.grapheus.web.component.operation.dialog.AbstractNewGraphPanel;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GenerateCollapsedGraphPanel extends AbstractNewGraphPanel {
    private final DropDownChoice<String> propertySelector;
    private final String sourceGraphId;
    private String property;

    public GenerateCollapsedGraphPanel(String id, String graphId, String vertexId) {
        super(id);

        this.sourceGraphId = graphId;
        propertySelector = new DropDownChoice<>("property", newPropertiesModel(graphId, vertexId));
    }

    private IModel<List<String>> newPropertiesModel(String graphName, String vertexId) {
        return new LoadableDetachableModel<List<String>>() {
            @Override
            protected List<String> load() {
                RVertex vertex = RemoteUtil.vertexAPI().getVertex(graphName, vertexId);
                return vertex == null
                        ? Collections.emptyList()
                        : vertex.getProperties().stream()
                            .map(RVertex.RProperty::getName)
                            .distinct()
                            .collect(Collectors.toList());
            }
        };
    }

    @Override
    protected void populateForm(Form<Object> form) {
        super.populateForm(form);
        form.add(propertySelector);
    }

    @Override
    protected void createGraph() {
        RemoteUtil.operationAPI().generateCollapsedGraph(sourceGraphId, newGraphName, property);
    }
}
