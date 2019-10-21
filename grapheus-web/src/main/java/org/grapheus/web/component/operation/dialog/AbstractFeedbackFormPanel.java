package org.grapheus.web.component.operation.dialog;

import org.apache.wicket.ajax.AjaxPreventSubmitBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

abstract public class AbstractFeedbackFormPanel extends Panel {
    private static final String FIELD_FORM_GENERATOR = "generatorForm";

    private Form<Object> form;
    private FeedbackPanel feedback;

    public AbstractFeedbackFormPanel(String id) {
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

    protected abstract void populateForm(Form<Object> form);
    protected abstract void doOperation(AjaxRequestTarget target);

    private Form<Object> newGraphCreationForm(String id) {
        return new Form<>(id, new CompoundPropertyModel<>(this));
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
                doOperation(target);
                target.add(feedback);
            }

        };
    }
}
