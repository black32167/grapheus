/**
 * 
 */
package org.grapheus.web.component.shared;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.model.IModel;

/**
 * @author black
 *
 */
public class LambdaAjaxCheckbox extends AjaxCheckBox {
    private static final long serialVersionUID = 1L;
    private SerializableConsumer<AjaxRequestTarget> clickCallback;

    public LambdaAjaxCheckbox(String id, SerializableConsumer<AjaxRequestTarget> clickCallback) {
        super(id);
        this.clickCallback = clickCallback;
    }

    public LambdaAjaxCheckbox(String id, IModel<Boolean> checkboxModel, SerializableConsumer<AjaxRequestTarget> clickCallback) {
        super(id, checkboxModel);
        this.clickCallback = clickCallback;
    }
    
    @Override
    protected void onUpdate(AjaxRequestTarget target) {
        clickCallback.accept(target);
    }
}
