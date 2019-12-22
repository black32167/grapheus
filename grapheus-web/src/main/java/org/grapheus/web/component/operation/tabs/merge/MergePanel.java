/**
 * 
 */
package org.grapheus.web.component.operation.tabs.merge;

import lombok.Builder;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.grapheus.web.RemoteUtil;
import org.grapheus.web.component.operation.tabs.AbstractEmbeddedPanel;
import org.grapheus.web.component.shared.vlist.VerticesListArgumentPanel;
import org.grapheus.web.page.vertices.list.VerticesPage;

/**
 * @author black
 *
 */
public class MergePanel extends AbstractEmbeddedPanel {
    private static final long serialVersionUID = 1L;
    
    private VerticesListArgumentPanel verticesList;

    private final IModel<String> graphIdModel;
    private String newVertexName;

    @Builder
    public MergePanel(String id, IModel<String> graphIdModel) {
        super(id);
        this.graphIdModel = graphIdModel;
    }

    @Override
    protected void populateForm(Form<Object> form) {
        super.populateForm(form);
        form.add(verticesList = new VerticesListArgumentPanel("verticesList", "Vertices to collapse"));
        form.add(newVertexNameInput("newVertexName"));
    }
    
    private Component newVertexNameInput(String id) {
        return new TextField<>(id);
    }

    @Override
    protected void performOperation(AjaxRequestTarget target) {
        String graphId = graphIdModel.getObject();
        RemoteUtil.operationAPI().merge(
                graphId,
                newVertexName,
                verticesList.getVerticesIds());
        setResponsePage(VerticesPage.class, new PageParameters().add(VerticesPage.PARAM_SELECTED_GRAPH, graphId));
    }
}
