/**
 * 
 */
package org.grapheus.web.component.operation.dialog.copy;

import org.grapheus.web.RemoteUtil;
import org.grapheus.web.component.operation.dialog.AbstractNewGraphPanel;

/**
 * @author black
 *
 */
public class CopyGraphGenerationPanel extends AbstractNewGraphPanel {

    private static final long serialVersionUID = 1L;
    private final String sourceGraphName;

    public CopyGraphGenerationPanel(String id, String sourceGraphName) {
        super(id);
        this.sourceGraphName = sourceGraphName;
        this.newGraphName = sourceGraphName+"_copy";
    }

    @Override
    protected void createGraph() {
        RemoteUtil.operationAPI().generateCloneGraph(sourceGraphName, newGraphName);
    }

}
