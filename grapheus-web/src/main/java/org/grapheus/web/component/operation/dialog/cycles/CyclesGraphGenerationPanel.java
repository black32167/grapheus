/**
 * 
 */
package org.grapheus.web.component.operation.dialog.cycles;

import org.grapheus.web.RemoteUtil;
import org.grapheus.web.component.operation.dialog.AbstractNewGraphPanel;

/**
 * @author black
 */
public class CyclesGraphGenerationPanel extends AbstractNewGraphPanel {
    private static final long serialVersionUID = 1L;
    private final String sourceGraphName;

    public CyclesGraphGenerationPanel(String id, String sourceGraphName) {
        super(id);
        this.sourceGraphName = sourceGraphName;
        this.newGraphName = sourceGraphName+"_cycles";
    }

    @Override
    protected void createGraph() {
        RemoteUtil.operationAPI().generateCyclicGraph(sourceGraphName, newGraphName);
    }

}
