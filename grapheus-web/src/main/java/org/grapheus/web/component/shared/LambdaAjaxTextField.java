/**
 * 
 */
package org.grapheus.web.component.shared;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.attributes.ThrottlingSettings;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.util.time.Duration;

/**
 * @author black
 */
public class LambdaAjaxTextField<T> extends TextField<T> {
    private static final long serialVersionUID = 1L;
    private SerializableConsumer<IPartialPageRequestHandler> changeCallback;

    public LambdaAjaxTextField(String id, SerializableConsumer<IPartialPageRequestHandler> changeCallback) {
        super(id);
        this.changeCallback = changeCallback;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        
        add(new OnChangeAjaxBehavior(){
            private static final long serialVersionUID=1L;

            @Override
            protected void onUpdate(final AjaxRequestTarget target) {
                changeCallback.accept(target);
            }
            
            @Override
            protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                super.updateAjaxAttributes(attributes);
                attributes.setThrottlingSettings(
                    new ThrottlingSettings(Duration.ONE_SECOND, true)
                );
            }
        });
    }

}
