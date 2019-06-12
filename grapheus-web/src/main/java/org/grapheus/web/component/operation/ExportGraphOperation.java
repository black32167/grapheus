/**
 * 
 */
package org.grapheus.web.component.operation;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.grapheus.web.RemoteUtil;
import org.grapheus.web.behaviour.AjaxDownloadBehavior;

/**
 * @author black
 */
public class ExportGraphOperation implements GraphOperation {
    private final static String MIME_ZIP = "application/zip";
    
    private static final long serialVersionUID = 1L;
    
    private final AjaxDownloadBehavior ajaxDownloadBehavior;
    
    public ExportGraphOperation(String graphName, Page page) {
        this.ajaxDownloadBehavior = AjaxDownloadBehavior.builder()
                .fileName(graphName+".zip")
                .contentType(MIME_ZIP)
                .inputStreamSupplier(() -> RemoteUtil.graphsAPI().export(graphName))
                .build();
        page.add(ajaxDownloadBehavior);
    }
    
    @Override
    public boolean apply(AjaxRequestTarget target) {
        ajaxDownloadBehavior.initiate(target);
        return true;
    }

}
