/**
 * 
 */
package org.grapheus.web.component.operation.dialog.empty;

import org.grapheus.web.RemoteUtil;
import org.grapheus.web.component.operation.dialog.AbstractNewGraphPanel;

/**
 * @author black
 *
 */
public class EmptyGraphGenerationPanel extends AbstractNewGraphPanel {

    private static final long serialVersionUID = 1L;

    public EmptyGraphGenerationPanel(String id) {
        super(id);
    }

    @Override
    protected void createGraph() {
        RemoteUtil.operationAPI().generateEmptyGraph(newGraphName);
    }

}
