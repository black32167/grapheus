/**
 * 
 */
package grapheus.persistence.conpool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.ArangoDatabase;
import com.arangodb.velocypack.VPackBuilder;
import com.arangodb.velocypack.VPackDeserializationContext;
import com.arangodb.velocypack.VPackSerializationContext;
import com.arangodb.velocypack.VPackSlice;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import grapheus.persistence.exception.DatabaseNotFoundException;
import grapheus.persistence.exception.StorageException;
import grapheus.server.config.DBConfig;

/**
 * @author black
 *
 */
@Service
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
@Slf4j
public class DBConnectionPoolImpl implements DBConnectionPool, DBConnectionPoolManager {

    @RequiredArgsConstructor
    private static class Connection {
        private static final AtomicInteger ccount = new AtomicInteger();
        @Getter final int id = ccount.incrementAndGet();
        @Getter final ArangoDB arangoDriver;
        
        final String dbName;
        
        @Getter boolean valid;
        
        void invalidate() {valid = false;}
        void valid() {valid = true;}
        
        public ArangoDatabase db() {
            return arangoDriver.db(dbName);
        }
    }
    
    private static final int MAX_PARALLEL_REQUESTS = 1;
    private static final long RECONNECT_SLEEP = 1000;
    private static final long SHUTDOWN_TIMEOUT = 5000;
    private static final int CONN_RQ_TIMEOUT = 30000;
    
    private final BlockingQueue<Connection> availableConnections = new ArrayBlockingQueue<>(MAX_PARALLEL_REQUESTS);

    private final Object initMonitor = new Object();
    
    private ArangoDB.Builder arangoBuilder;

    private final DBConfig dbConfig;
    
    private volatile boolean dbInitialized;
    private volatile boolean shutdown;

    @PostConstruct
    void init() throws Exception {
        log.info("Initializing ArangoDB");
        log.info("Connecting database on host {} and port {}", dbConfig.getDbHost(), dbConfig.getDbPort());
        
        arangoBuilder = new ArangoDB.Builder().//
            maxConnections(1).
            host(dbConfig.getDbHost(), dbConfig.getDbPort()).//
            user(dbConfig.getDbUser()).//
            password(dbConfig.getDbPassword()).//
            timeout(CONN_RQ_TIMEOUT).
            registerSerializer(byte[].class, (VPackBuilder builder, String attribute, byte[] value, VPackSerializationContext context) -> {
                builder.add(attribute, value);
            }).
            registerDeserializer(byte[].class, (VPackSlice parent, VPackSlice vpack, VPackDeserializationContext context) -> {
                return vpack.getAsBinary();
            });

        for(int i = 0; i < MAX_PARALLEL_REQUESTS; i++) {
            Connection connection = new Connection(arangoBuilder.build(), dbConfig.getDbName());
            connection.valid();
            availableConnections.add(connection);
        }
    }

    @PreDestroy
    private void shutdown() {
        shutdown = true;
        for(int i = 0; i < MAX_PARALLEL_REQUESTS; i++) {
            try {
                Connection c = availableConnections.poll(SHUTDOWN_TIMEOUT, TimeUnit.MILLISECONDS);
                if(c == null) {
                    break;
                }
                c.arangoDriver.shutdown();
            } catch (Exception e) {
            }
        }
       
        log.info("Database connections are shut down");
    }

   
    @Override
    public void update(DBConnectionUpdateConsumer connectionConsumer) throws StorageException {
        query((db) -> {
            connectionConsumer.accept(db);
            return null;
        });
        
    }
    @Override
    public <T> T query(DBConnectionConsumer<T> connectionConsumer) throws StorageException {
        if(shutdown) {
            throw new StorageException("Cannot lend connection during shutdown process.");
        }
        // Acquire connection
        Connection connection;
        try {
            connection = availableConnections.take();
        } catch (InterruptedException e) {
            log.warn("Waiting on connection pool is interrupted", e);
            throw new StorageException("Waiting on onnection pool is interrupted", e);
        }
        
        // Ensure connection is valid
    
        while(!shutdown) {
            while (!connection.isValid()) {
                try {
                    if(!dbInitialized) {
                        synchronized(initMonitor) {
                            while(!dbInitialized) {
                                log.info("Database is not initialized yet - waiting...");
                                initMonitor.wait();
                            }
                        }
                    }
                    ArangoDatabase db = connection.db();
                    db.reloadRouting();
                    connection.valid();
                } catch (Exception e) {
                    log.error("Could not reconnect to the database:{}", e.getMessage());
                    try {
                        Thread.sleep(RECONNECT_SLEEP);
                    } catch (InterruptedException e1) {}   
                }
            }
            
            // Consume connection
            try {
                return connectionConsumer.accept(connection.db());
            } catch (DatabaseNotFoundException dbe) {
                connection.invalidate();
            } catch (Exception e) {
                log.error("Error running DB request:{}", e.getMessage());
                
                throw e;
            } finally {
                try {
                    availableConnections.put(connection);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        throw new IllegalStateException();
    }


    @Override
    public void withDriver(DBDriverConsumer driverConsumer) {   
        boolean done = false;
        while(!done &&!shutdown) {
            ArangoDB driver = null;
            try {
                driver = arangoBuilder.build();
                driverConsumer.accept(driver);
                done = true;
            } catch (ArangoDBException e) {
                log.error("Could not initilize the database:{}", e.getMessage());
                try {
                    Thread.sleep(RECONNECT_SLEEP);
                } catch (InterruptedException e1) {}
            } finally {
                if(driver != null) try {driver.shutdown();} catch (Exception e) {}
            }
        }
    }

    @Override
    public void enablePool() {
        dbInitialized = true;
        synchronized (initMonitor) {
            dbInitialized = true;
            initMonitor.notifyAll();
        }
    }

    @Override
    public int countAvailableConnections() {
        return availableConnections.size();
    }

}
