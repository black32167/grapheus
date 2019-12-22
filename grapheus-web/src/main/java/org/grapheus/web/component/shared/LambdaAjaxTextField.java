/**
 * 
 */
package org.grapheus.web.component.shared;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.attributes.ThrottlingSettings;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.util.time.Duration;
import org.grapheus.web.state.event.GraphViewChangedEvent;

/**
 * @author black
 */
public class LambdaAjaxTextField<T> extends TextField<T> {
    private static final long serialVersionUID = 1L;

    public LambdaAjaxTextField(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        
        add(new OnChangeAjaxBehavior(){
            private static final long serialVersionUID=1L;

            @Override
            protected void onUpdate(final AjaxRequestTarget target) {
                send(LambdaAjaxTextField.this, Broadcast.BUBBLE, new GraphViewChangedEvent(target));
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
