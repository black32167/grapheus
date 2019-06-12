/**
 * 
 */
package grapheus.persistence.storage.graph;

/**
 * @author black
 *
 */
public final class GraphNameUtils {

    public static String edgesCollectionName(String graphName) {
        return "E_" + graphName;
    }    
    public static String verticesCollectionName(String graphName) {
        return "V_" + graphName;
    }
    
    private GraphNameUtils() {}
}
