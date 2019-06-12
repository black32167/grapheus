/**
 * 
 */
package org.grapheus.web.component.shared;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.form.NumberTextField;

/**
 * @author black
 *
 */
public class LambdaAjaxNumberField<N extends Number & Comparable<N>> extends NumberTextField<N> {
 
    private static final long serialVersionUID = 1L;
    
    private final SerializableConsumer<AjaxRequestTarget> changeCallback;

    public LambdaAjaxNumberField(String id, SerializableConsumer<AjaxRequestTarget> changeCallback) {
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
        });
    }


}
