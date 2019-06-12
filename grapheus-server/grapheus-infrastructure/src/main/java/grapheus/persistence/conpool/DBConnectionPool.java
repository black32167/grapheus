/**
 * 
 */
package grapheus.persistence.conpool;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;

import grapheus.persistence.exception.StorageException;

/**
 * @author black
 *
 */
public interface DBConnectionPool {

    @FunctionalInterface
    public interface DBConnectionConsumer<T> {
        T accept(ArangoDatabase db);
    }

    @FunctionalInterface
    public interface DBConnectionUpdateConsumer {
        void accept(ArangoDatabase db);
    }
    
    @FunctionalInterface
    public interface DBDriverConsumer {
        void accept(ArangoDB driver);
    }
    
    
    <T> T query(DBConnectionConsumer<T> connectionConsumer) throws StorageException;
    void update(DBConnectionUpdateConsumer connectionConsumer) throws StorageException;
    void withDriver(DBDriverConsumer driverConsumer);
    int countAvailableConnections();
    
}
