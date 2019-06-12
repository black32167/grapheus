/**
 * 
 */
package grapheus.service.uds;

/**
 * @author black
 *
 */
public class SourceAlreadyExistException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SourceAlreadyExistException(String message) {
        super(message);
    }

}
