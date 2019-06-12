/**
 * 
 */
package org.grapheus.web.component.shared;

import java.io.Serializable;

/**
 * @author black
 */
@FunctionalInterface
public interface SerializableSupplier<T> extends Serializable {
    T get();
}
