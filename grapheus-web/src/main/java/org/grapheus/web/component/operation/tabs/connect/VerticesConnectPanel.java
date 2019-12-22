/**
 * 
 */
package org.grapheus.web.component.operation.tabs.connect;

import lombok.Builder;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.grapheus.web.RemoteUtil;
import org.grapheus.web.component.operation.tabs.AbstractEmbeddedPanel;
import org.grapheus.web.component.shared.vlist.VerticesListArgumentPanel;

/**
 * @author black
 */
public class VerticesConnectPanel extends AbstractEmbeddedPanel {
    private static final long serialVersionUID = 1L;
    
    private VerticesListArgumentPanel fromList;
    private VerticesListArgumentPanel toList;
    private final String graphId;
    
    @Builder
    public VerticesConnectPanel(String id, String graphId) {
        super(id);
        this.graphId = graphId;
    }

    @Override
    protected void populateForm(Form<Object> form) {
        super.populateForm(form);
        form.add(fromList = new VerticesListArgumentPanel("fromList", "From"));
        form.add(toList = new VerticesListArgumentPanel("toList", "To"));
    }
    
    @Override
    protected void performOperation(AjaxRequestTarget target) {
        RemoteUtil.operationAPI().connect(
                graphId,
                fromList.getVerticesIds(),
                toList.getVerticesIds());
        fromList.clear();
        toList.clear();
    }

}
