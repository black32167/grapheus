/**
 * 
 */
package grapheus.persistence.exception;

/**
 * @author black
 *
 */
public class DocumentExistsException extends StorageException {

    private static final long serialVersionUID = 1L;

    public DocumentExistsException(String message) {
        super(message);
    }

    public DocumentExistsException(String msg, Exception e) {
       super(msg, e);
    }

}
