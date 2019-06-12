/**
 * 
 */
package org.grapheus.web;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * @author black
 *
 */
public interface ShowOperationSupport extends Serializable {
    void showOperation(AjaxRequestTarget target, Component operationComponent);
    void finishOperation(AjaxRequestTarget target);
    String getId();
}
