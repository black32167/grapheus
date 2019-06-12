/**
 * 
 */
package grapheus.event;

import com.arangodb.ArangoDatabase;

/**
 * Listener is invoked when database connection is established.
 * 
 * @author black
 */
public interface OnDbConnectionListener {
    void onConnected(ArangoDatabase db);
}
