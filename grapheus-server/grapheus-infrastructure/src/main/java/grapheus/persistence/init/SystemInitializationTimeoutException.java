/**
 * 
 */
package grapheus.persistence.init;

/**
 * @author black
 *
 */
public class SystemInitializationTimeoutException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SystemInitializationTimeoutException(String message) {
        super(message);
    }
    
}
