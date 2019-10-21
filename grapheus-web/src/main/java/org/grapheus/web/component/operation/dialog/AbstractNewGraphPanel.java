/**
 * 
 */
package org.grapheus.web.component.operation.dialog;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.grapheus.web.page.vertices.list.VerticesPage;

/**
 * @author black
 *
 */
abstract public class AbstractNewGraphPanel extends AbstractFeedbackFormPanel {
    private static final long serialVersionUID = 1L;
    private static final String INPUT_NEW_GRAPH_NAME = "newGraphName";

    protected String newGraphName = "new_" + System.currentTimeMillis();

    public AbstractNewGraphPanel(String id) {
        super(id);
    }

    protected void populateForm(Form<Object> form) {
        form.add(new TextField<>(INPUT_NEW_GRAPH_NAME).setRequired(true));
    }

    @Override
    protected void doOperation(AjaxRequestTarget target) {
        try {
            createGraph();
            setResponsePage(VerticesPage.class, new PageParameters().add(VerticesPage.PARAM_SELECTED_GRAPH, newGraphName));
        } catch (Exception e) {
            error("Could not create the graph: " + e.getMessage());
        }
    }

    protected abstract void createGraph();
}
