package org.grapheus.web.component.operation.dialog.filter.property;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.grapheus.client.model.graph.vertex.RVertex;
import org.grapheus.web.RemoteUtil;
import org.grapheus.web.component.operation.dialog.AbstractFeedbackFormPanel;
import org.grapheus.web.page.vertices.list.VerticesPage;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FilterByPropertyPanel extends AbstractFeedbackFormPanel {
    private final DropDownChoice<String> propertySelector;
    private final DropDownChoice<String> valueSelector;
    private final String sourceGraphId;
    private String property;
    private String value;
    private IModel<RVertex> vertexModel;

    public FilterByPropertyPanel(String id, String graphId, String vertexId) {
        super(id);

        this.sourceGraphId = graphId;
        vertexModel = vertexModel(graphId, vertexId);
        IModel<List<String>> propModel = newPropertiesModel();
        propertySelector = new DropDownChoice<String>("property", propModel);
        propertySelector.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(valueSelector);
            }
        });
        valueSelector = new DropDownChoice<>("value", newValuesModel());
        valueSelector.setOutputMarkupId(true);
    }

    private IModel<RVertex> vertexModel(String graphName, String vertexId) {
        return new LoadableDetachableModel<RVertex>() {
            @Override
            protected RVertex load() {
                RVertex vertex = RemoteUtil.vertexAPI().getVertex(graphName, vertexId);
                return vertex;
            }
        };
    }

    private IModel<List<String>> newPropertiesModel() {
        return new LoadableDetachableModel<List<String>>() {
            @Override
            protected List<String> load() {
                RVertex vertex = vertexModel.getObject();
                return vertex == null
                        ? Collections.emptyList()
                        : vertex.getProperties().stream()
                            .map(RVertex.RProperty::getName)
                            .distinct()
                            .collect(Collectors.toList());
            }
        };
    }

    private IModel<List<String>> newValuesModel() {
        return new LoadableDetachableModel<List<String>>() {
            @Override
            protected List<String> load() {
                RVertex vertex = vertexModel.getObject();
                return vertex == null || property == null
                        ? Collections.emptyList()
                        : vertex.getProperties().stream()
                        .filter(p->property.equals(p.getName()))
                        .map(RVertex.RProperty::getValue)
                        .distinct()
                        .collect(Collectors.toList());
            }
        };
    }

    @Override
    protected void populateForm(Form<Object> form) {
        form.add(propertySelector);
        form.add(valueSelector);
    }

    @Override
    protected void doOperation(AjaxRequestTarget target) {
        //TODO: implement filtering by updatinf model/invoking calback
        setResponsePage(VerticesPage.class, new PageParameters()
                .add(VerticesPage.PARAM_FILTER_PROPERTY, property+"="+value)
                .add(VerticesPage.PARAM_SELECTED_GRAPH, sourceGraphId));
    }
}
