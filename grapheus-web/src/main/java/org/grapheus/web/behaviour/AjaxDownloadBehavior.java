/**
 * 
 */
package org.grapheus.web.behaviour;

import java.io.InputStream;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.time.Duration;
import org.grapheus.web.component.shared.SerializableSupplier;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

/**
 * @author black
 *
 */
@RequiredArgsConstructor
@Builder
public class AjaxDownloadBehavior extends AbstractAjaxBehavior {

    private static final long serialVersionUID = 1L;
    
    private final String fileName;
    private final String contentType;
    private final SerializableSupplier<InputStream> inputStreamSupplier;
    
    public void initiate(AjaxRequestTarget target) {
        String url = getCallbackUrl().toString();
        target.appendJavaScript("setTimeout(\"window.location.href='" + url + "'\", 100);");
    }

    @Override
    public void onRequest() {
        ResourceStreamRequestHandler handler = new ResourceStreamRequestHandler(
                new ReusableResourceStream(contentType, inputStreamSupplier),
                fileName);
        handler.setContentDisposition(ContentDisposition.ATTACHMENT);
        handler.setCacheDuration(Duration.NONE);
        RequestCycle.get().scheduleRequestHandlerAfterCurrent(handler);

    }

}
