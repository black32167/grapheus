/**
 * 
 */
package org.grapheus.web.component.operation;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.grapheus.web.RemoteUtil;

import lombok.RequiredArgsConstructor;

/**
 * @author black
 *
 */
@RequiredArgsConstructor
public class ToposortOpertion implements GraphOperation {
    private static final long serialVersionUID = 1L;
    private final String graphName;

    @Override
    public boolean apply(AjaxRequestTarget target) {
        RemoteUtil.operationAPI().generateTopologicalOrder(graphName);
        return false;
    }

}
