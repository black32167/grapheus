/**
 * 
 */
package org.grapheus.web.component.list.view;

import org.apache.wicket.ajax.AjaxRequestTarget;

import java.io.Serializable;
import java.util.List;

/**
 * @author black
 *
 */
@FunctionalInterface
public interface VertexRemovalListener extends Serializable {
	void onVerticesRemoved(AjaxRequestTarget target, List<String> removedVerticesIds);
}
