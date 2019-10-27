/**
 * 
 */
package org.grapheus.web.component.operation.tabs.rooted;

import lombok.Builder;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.grapheus.web.RemoteUtil;
import org.grapheus.web.component.operation.tabs.AbstractEmbeddedPanel;
import org.grapheus.web.component.shared.SerializableConsumer;
import org.grapheus.web.component.shared.vlist.VerticesListArgumentPanel;
import org.grapheus.web.page.vertices.list.VerticesPage;

import java.util.List;

/**
 * @author black
 */
public class RootedSubgraphGenerationPanel extends AbstractEmbeddedPanel {

    private static final long serialVersionUID = 1L;
    
    private VerticesListArgumentPanel verticesList;

    private String connectedSign = "???";
    private String newGraphName;

    private final IModel<String> sourceGraphIdModel;
    private final Component connectedLabel;
    
    @Builder
    public RootedSubgraphGenerationPanel(String id, SerializableConsumer<AjaxRequestTarget> operationFinishedCallback, IModel<String> sourceGraphIdModel) {
        super(id, operationFinishedCallback);
        
        this.sourceGraphIdModel = sourceGraphIdModel;
        //TODO: replace by CompoundModel?
        this.connectedLabel = new Label("connectedLabel", new PropertyModel<String>(this, "connectedSign")).setOutputMarkupId(true);
    }
    
    protected void populateForm(Form<Object> form) {
        super.populateForm(form);
        form.add(verticesList = new VerticesListArgumentPanel("verticesList", "Boundary vertices").setChangeCallback(this::checkConnection));
        newGraphName = getSourceGraphId() + "_rooted";
        form.add(new TextField<String>("newGraphName"));
        form.add(connectedLabel);
    }

    @Override
    protected void performOperation(AjaxRequestTarget target) {
        String sourceGraphId = getSourceGraphId();
        List<String> verticesIds = verticesList.getVerticesIds();
        RemoteUtil.operationAPI().generatePathGraph(
                sourceGraphId, newGraphName, verticesIds);
        setResponsePage(VerticesPage.class, new PageParameters().add(VerticesPage.PARAM_SELECTED_GRAPH, newGraphName));
    }
    
    private void checkConnection(AjaxRequestTarget target) {
        String sourceGraphId = getSourceGraphId();
        List<String> veticesIds = verticesList.getVerticesIds();
        connectedSign = "---";
        if(veticesIds.size() == 2) {
            List<String> path = RemoteUtil.operationAPI().shortestPath(
                    sourceGraphId,
                    veticesIds.get(0),
                    veticesIds.get(1));
            if(path.isEmpty()) {
                path = RemoteUtil.operationAPI().shortestPath(
                        sourceGraphId,
                        veticesIds.get(1),
                        veticesIds.get(0));
            }
            if(path.size() > 0) {
                connectedSign = "Connected via " + String.valueOf(path.size()-2) + " hops";
            }
        }
        target.add(connectedLabel);
    }

    private String getSourceGraphId() {
        return sourceGraphIdModel.getObject();
    }

}
