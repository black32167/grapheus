/**
 * 
 */
package org.grapheus.web.component.operation.dialog.self;

import org.grapheus.web.RemoteUtil;
import org.grapheus.web.component.operation.dialog.AbstractNewGraphPanel;

/**
 * @author black
 *
 */
public class SelfGraphGenerationPanel extends AbstractNewGraphPanel {

    private static final long serialVersionUID = 1L;

    public SelfGraphGenerationPanel(String id) {
        super(id);
        this.newGraphName = "self";
    }

    @Override
    protected void createGraph() {
        RemoteUtil.operationAPI().generateSelfGraph(newGraphName);
    }

}
