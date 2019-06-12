/**
 * 
 */
package grapheus.persistence.exception;

/**
 * @author black
 *
 */
public class DocumentsConflictException extends StorageException {

    private static final long serialVersionUID = 1L;

    public DocumentsConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    public DocumentsConflictException(String message) {
        super(message);
    }

    public DocumentsConflictException(Throwable cause) {
        super(cause);
    }

}
