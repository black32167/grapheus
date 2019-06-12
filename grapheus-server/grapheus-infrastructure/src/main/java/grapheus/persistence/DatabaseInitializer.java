/**
 * 
 */
package grapheus.persistence;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import grapheus.event.DatabaseInitializedListener;
import grapheus.event.OnAfterDbConnectionListener;
import grapheus.persistence.conpool.DBConnectionPool;
import grapheus.persistence.conpool.DBConnectionPoolManager;
import grapheus.persistence.init.SystemInitializationTimeoutException;
import grapheus.server.config.DBConfig;
import grapheus.utils.ListenerUtils;

/**
 * @author black
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer implements ReadyForServingRequestsLatch {
    private final static long INITIALIZATION_TIMEOUT_MS = 5000;
    
    private final DBConnectionPool dbTemplate;
    private final DBConfig dbConfig;
    
    @Autowired(required=false)
    private List<OnAfterDbConnectionListener> afterDbConnectionListeners;
   
    @Autowired(required=false)
    private List<DatabaseInitializedListener> databaseInitializedListeners;
    
    @Inject
    private DBConnectionPoolManager connPoolManager;
    
    private final CountDownLatch fullyInitializedLatch = new CountDownLatch(1);
    
    @PostConstruct
    private void initDB() {
        dbTemplate.withDriver(driver -> {
            if(!driver.getAccessibleDatabases().contains(dbConfig.getDbName())) {
                log.info("Initializing database '{}'", dbConfig.getDbName());
                driver.createDatabase(dbConfig.getDbName());
            }
            ListenerUtils.iterateLogExceptions(afterDbConnectionListeners, l -> l.onConnected(driver.db(dbConfig.getDbName())));
        });

        connPoolManager.enablePool();
        
        ListenerUtils.iterateLogExceptions(databaseInitializedListeners, l -> l.databaseInitialized());
        
        fullyInitializedLatch.countDown();
    }

    @Override
    public void waitUntilReady() {
        try {
            if(!fullyInitializedLatch.await(INITIALIZATION_TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
                throw new SystemInitializationTimeoutException("Could not initialize system in " + INITIALIZATION_TIMEOUT_MS + " ms.");
            }
        } catch (InterruptedException e) {
            throw new SystemInitializationTimeoutException("Interrupted while waiting for system initialization");
        }
        
    }
}
