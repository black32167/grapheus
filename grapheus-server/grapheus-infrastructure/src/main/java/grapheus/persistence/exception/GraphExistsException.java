/**
 * 
 */
package grapheus.persistence.exception;

/**
 * @author black
 *
 */
public class GraphExistsException extends Exception {
    private static final long serialVersionUID = 1L;
    
    public GraphExistsException(String graphName) {
        super("Graph with name '" + graphName + "' already exists");
    }
}
