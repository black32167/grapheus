/**
 * 
 */
package org.grapheus.web.component.list.view;

import java.io.Serializable;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * @author black
 *
 */
@FunctionalInterface
public interface VertexSelectionListener extends Serializable {
	void onVertexSelected(AjaxRequestTarget target, String artifactId);

}
