/**
 * 
 */
package org.grapheus.web.component.shared;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;


/**
 * @author black
 *
 */
public class LambdaAjaxLink extends AjaxLink<Void> {
    private static final long serialVersionUID = 1L;
    private SerializableConsumer<AjaxRequestTarget> callback;

    public LambdaAjaxLink(String id, SerializableConsumer<AjaxRequestTarget> clickCallback) {
        super(id);
        this.callback = clickCallback;
    }

    @Override
    public void onClick(AjaxRequestTarget target) {
        callback.accept(target);
    }

}
