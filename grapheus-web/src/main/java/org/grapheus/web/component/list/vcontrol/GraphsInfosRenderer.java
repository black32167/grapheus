/**
 * 
 */
package org.grapheus.web.component.list.vcontrol;

import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.grapheus.web.model.GraphInfo;

import java.util.List;

/**
 * @author black
 */
public final class GraphsInfosRenderer implements IChoiceRenderer<GraphInfo> {
    private static final long serialVersionUID = 1L;
    public final static GraphsInfosRenderer INSTANCE = new GraphsInfosRenderer();

    private GraphsInfosRenderer() {}
    
    @Override
    public Object getDisplayValue(GraphInfo object) {
        return object.getGraphId();
    }

    @Override
    public String getIdValue(GraphInfo object, int index) {
        return object.getGraphId();
    }

    @Override
    public GraphInfo getObject(String id, IModel<? extends List<? extends GraphInfo>> choices) {
        return choices.getObject().stream().filter(g->g.getGraphId().equals(id)).findFirst().get();
    }
}
