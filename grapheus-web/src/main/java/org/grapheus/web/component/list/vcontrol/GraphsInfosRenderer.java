/**
 * 
 */
package org.grapheus.web.component.list.vcontrol;

import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.grapheus.web.model.GraphView;

import java.util.List;

/**
 * @author black
 */
public final class GraphsInfosRenderer implements IChoiceRenderer<GraphView> {
    private static final long serialVersionUID = 1L;
    public final static GraphsInfosRenderer INSTANCE = new GraphsInfosRenderer();

    private GraphsInfosRenderer() {}
    
    @Override
    public Object getDisplayValue(GraphView object) {
        return object.getGraphId();
    }

    @Override
    public String getIdValue(GraphView object, int index) {
        return object.getGraphId();
    }

    @Override
    public GraphView getObject(String id, IModel<? extends List<? extends GraphView>> choices) {
        return choices.getObject().stream().filter(g->g.getGraphId().equals(id)).findFirst().get();
    }
}
