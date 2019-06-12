/**
 * 
 */
package grapheus.view.extract.util;

import java.util.function.Consumer;

/**
 * Splits text to tokens.
 * 
 * @author black
 */
public interface TextTokenizer {
    /**
     * NOTE: wordConsumer MUST be thread-friendly.
     */
    void tokenize(String text, Consumer<String> wordConsumer);
}
