/**
 * 
 */
package grapheus.persistence.exception;

/**
 * @author black
 *
 */
public class GraphGenerationException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public GraphGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
    public GraphGenerationException(String message) {
        super(message);
    }
}
