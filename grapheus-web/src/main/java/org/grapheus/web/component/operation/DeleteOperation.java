/**
 * 
 */
package org.grapheus.web.component.operation;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.grapheus.web.RemoteUtil;

import lombok.RequiredArgsConstructor;

/**
 * @author black
 */
@RequiredArgsConstructor
public class DeleteOperation implements GraphOperation {
    private static final long serialVersionUID = 1L;
    private final String graphName;
    
    @Override
    public boolean apply(AjaxRequestTarget target) {
        RemoteUtil.graphsAPI().delete(graphName);
        return false;
    }

}
