/**
 * 
 */
package grapheus.persistence.exception;

/**
 * @author black
 *
 */
public class CollectionNotFoundException extends StorageException {

    private static final long serialVersionUID = 1L;

    public CollectionNotFoundException(String message) {
        super(message);
    }

    public CollectionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
