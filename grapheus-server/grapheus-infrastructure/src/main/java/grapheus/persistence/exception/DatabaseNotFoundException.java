/**
 * 
 */
package grapheus.persistence.exception;

/**
 * @author black
 *
 */
public class DatabaseNotFoundException extends StorageException {

    private static final long serialVersionUID = 1L;

    public DatabaseNotFoundException(String message, Throwable cause) {
        super(message, cause);
     
    }

    public DatabaseNotFoundException(Throwable cause) {
        super(cause);
       
    }

    public DatabaseNotFoundException(String message) {
        super(message);
      
    }

}
