/**
 * 
 */
package org.grapheus.web.component.list.filter;

import java.util.List;

import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.grapheus.client.model.graph.VerticesSortCriteriaType;

/**
 * @author black
 */
public final class VerticesSortTypesRenderer implements IChoiceRenderer<VerticesSortCriteriaType> {
    private static final long serialVersionUID = 1L;
    public final static VerticesSortTypesRenderer INSTANCE = new VerticesSortTypesRenderer();

    private VerticesSortTypesRenderer() {}
    
    @Override
    public Object getDisplayValue(VerticesSortCriteriaType object) {
        return object.getAlias();
    }

    @Override
    public String getIdValue(VerticesSortCriteriaType object, int index) {
        return object.name();
    }

    @Override
    public VerticesSortCriteriaType getObject(String id, IModel<? extends List<? extends VerticesSortCriteriaType>> choices) {
        return VerticesSortCriteriaType.valueOf(id);
    }

}
