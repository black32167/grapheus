/**
 * 
 */
package grapheus.view.extract.util;

/**
 * Provides predicate determines if the specific word should is not a meaningful term.
 * 
 * @author black
 *
 */
public interface StopWordRegistry {
    /**
     * Determines if the specific word should is not a meaningful term.
     */
    boolean isStopWord(String word);
}
