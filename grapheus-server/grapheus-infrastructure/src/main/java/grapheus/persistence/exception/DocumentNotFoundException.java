/**
 * 
 */
package grapheus.persistence.exception;

/**
 * @author black
 *
 */
public class DocumentNotFoundException extends StorageException {

    private static final long serialVersionUID = 1L;

    public DocumentNotFoundException(String message) {
        super(message);
    }

    public DocumentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
