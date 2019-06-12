/**
 * 
 */
package org.grapheus.web.component.operation;

import java.io.Serializable;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * @author black
 *
 */
public interface GraphOperation extends Serializable {
    /**
     * @return true if operation implementation has handled UI refresh.
     */
    boolean apply(AjaxRequestTarget target);
}
