/**
 * 
 */
package grapheus.persistence;

/**
 * @author black
 *
 */
public final class ArangoDBErrors {
    
    public final static int ERROR_ARANGO_CONFLICT = 1200;
    public final static int DB_NOT_FOUND = 1228;
    public final static int UNIQUE_CONSTRAINT_VIOLATED = 1210;
    public final static int DUPLICATE_NAME = 1207;
    public final static int COLLECTION_NOT_FOUND = 1203;

    private ArangoDBErrors() {}

}
