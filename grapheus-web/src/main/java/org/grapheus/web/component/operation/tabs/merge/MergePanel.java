/**
 * 
 */
package org.grapheus.web.component.operation.tabs.merge;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.grapheus.web.RemoteUtil;
import org.grapheus.web.component.operation.tabs.AbstractEmbeddedPanel;
import org.grapheus.web.component.shared.SerializableConsumer;
import org.grapheus.web.component.shared.vlist.VerticesListArgumentPanel;
import org.grapheus.web.page.vertices.list.VerticesPage;

import lombok.Builder;

/**
 * @author black
 *
 */
public class MergePanel extends AbstractEmbeddedPanel {
    private static final long serialVersionUID = 1L;
    
    private VerticesListArgumentPanel verticesList;

    private final String graphId;
    private String newVertexName;

    @Builder
    public MergePanel(String id, SerializableConsumer<AjaxRequestTarget> operationFinishedCallback, String graphId) {
        super(id, operationFinishedCallback);
        this.graphId = graphId;
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
        RemoteUtil.operationAPI().merge(
                graphId,
                newVertexName,
                verticesList.getVerticesIds());
        setResponsePage(VerticesPage.class, new PageParameters().add(VerticesPage.PARAM_SELECTED_GRAPH, graphId));
    }
}
