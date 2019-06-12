/**
 * 
 */
package org.grapheus.web.component.operation.tabs;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.grapheus.web.component.shared.SerializableConsumer;

/**
 * @author black
 */
public abstract class AbstractEmbeddedPanel extends Panel {
    private static final long serialVersionUID = 1L;
    
    private final SerializableConsumer<AjaxRequestTarget> operationFinishedCallback;
    
    public AbstractEmbeddedPanel(String id, SerializableConsumer<AjaxRequestTarget> operationFinishedCallback) {
        super(id);
        this.operationFinishedCallback = operationFinishedCallback;
    }
    
    abstract protected void performOperation(AjaxRequestTarget target);

    @Override
    protected void onInitialize() {
        super.onInitialize();
        
        Form<Object> form = newForm("form");
        populateForm(form);
        add(form);
                
    }

    protected void populateForm(Form<Object> form) {
        form.add(newConnectButtom("submitButton"));
    }

    protected Form<Object> newForm(String formId) {
        return new Form<Object>( 
                formId, new CompoundPropertyModel<Object>(this));
    }

    private Component newConnectButtom(String id) {
        return new AjaxButton(id) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                performOperation(target);

                operationFinishedCallback.accept(target);
            }
        };
    }
}
