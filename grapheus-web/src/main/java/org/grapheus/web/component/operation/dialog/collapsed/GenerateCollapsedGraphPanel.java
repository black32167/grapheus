package org.grapheus.web.component.operation.dialog.collapsed;

import org.grapheus.web.RemoteUtil;
import org.grapheus.web.component.operation.dialog.AbstractNewGraphPanel;

public class GenerateCollapsedGraphPanel extends AbstractNewGraphPanel {
    private final String sourceGraphId;
    private String generativeProperty;

    public GenerateCollapsedGraphPanel(String id, String graphId, String generativeProperty) {
        super(id);

        newGraphName = graphId + "_" + generativeProperty;
        this.sourceGraphId = graphId;
        this.generativeProperty = generativeProperty;
    }

    @Override
    protected void createGraph() {
        RemoteUtil.operationAPI().generateCollapsedGraph(sourceGraphId, newGraphName, generativeProperty);
    }
}
