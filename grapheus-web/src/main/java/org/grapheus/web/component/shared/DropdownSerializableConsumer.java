/**
 * 
 */
package org.grapheus.web.component.shared;

import java.io.Serializable;

import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.model.IModel;

/**
 * @author black
 *
 */
public interface DropdownSerializableConsumer<T> extends Serializable {
    void accept(IPartialPageRequestHandler target, IModel<T> selectedModel);
}
