/**
 * 
 */
package org.grapheus.web.component.operation.dialog;

import org.apache.wicket.ajax.AjaxPreventSubmitBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.grapheus.web.page.vertices.list.VerticesPage;

/**
 * @author black
 *
 */
abstract public class AbstractNewGraphPanel extends Panel {
    private static final long serialVersionUID = 1L;
    private static final String INPUT_NEW_GRAPH_NAME = "newGraphName";
    
    private static final String FIELD_FORM_GENERATOR = "generatorForm";

    protected String newGraphName = "new_" + System.currentTimeMillis();
    private Form<Object> form;
    private FeedbackPanel feedback;

    public AbstractNewGraphPanel(String id) {
        super(id);
    }
    
    @Override
    protected void onInitialize() {
        super.onInitialize();
        add((feedback = new FeedbackPanel("feedback")).setOutputMarkupId(true));
       
        add((form = newGraphCreationForm(FIELD_FORM_GENERATOR))
                .add(new Button("submitButton").add(newAjaxSubmitBehavior())))
                .add(new AjaxPreventSubmitBehavior());
                
        populateForm(form);
    }

    private Form<Object> newGraphCreationForm(String fieldFormGenerator) {
        return new Form<Object>(FIELD_FORM_GENERATOR, new CompoundPropertyModel<>(this));
    }

    private AjaxFormSubmitBehavior newAjaxSubmitBehavior() {
        return new AjaxFormSubmitBehavior(form, "click") {

            private static final long serialVersionUID = 1L;
            
            @Override
            protected void onError(AjaxRequestTarget target) {
                target.add(feedback);
            }

            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                try {
                    createGraph();
                    setResponsePage(VerticesPage.class, new PageParameters().add(VerticesPage.PARAM_SELECTED_GRAPH, newGraphName));
                } catch (Exception e) {
                    error("Could not create the graph: " + e.getMessage());
                    target.add(feedback);
                }
            }
            
        };
    }

    protected abstract void createGraph();


    protected void populateForm(Form<Object> form) {
        form.add(new TextField<>(INPUT_NEW_GRAPH_NAME).setRequired(true));
    }
}
