/**
 * 
 */
package org.grapheus.web.component.shared;

import java.io.Serializable;

/**
 * @author black
 *
 */
@FunctionalInterface
public interface SerializableConsumer<T> extends Serializable {
    void accept(T t);
}
