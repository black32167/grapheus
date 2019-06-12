/**
 * 
 */
package grapheus.utils;

import java.util.function.Consumer;

import lombok.extern.slf4j.Slf4j;

/**
 * Auxiliary class for listeners traverse.
 * 
 * @author black
 *
 */
@Slf4j
public final class ListenerUtils {
    public static <T> void iterateLogExceptions(Iterable<T> elements, Consumer<T> elementConsumer) {
        if(elements != null) {
            for(T element: elements) {
                try {
                    elementConsumer.accept(element);
                } catch (Exception e) {
                    log.error("", e);
                }
            }
        }
    }

    private ListenerUtils() {
    }
    
}
