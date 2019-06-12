/**
 * 
 */
package grapheus.periodic;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.springframework.stereotype.Service;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import grapheus.TimeService;
import grapheus.concurrency.ExecutorServiceFactory;
import grapheus.persistence.conpool.DBConnectionPool;

/**
 * Service periodically collects the telemetry.
 * 
 * @author black
 */
@Service
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class TelemetryCollector {
    private final static String TELEMETRY_THREAD_NAME = "TelemetryCollector thread";
    
    private final TimeService ts;
    private final ExecutorServiceFactory execServiceFactory;
    private final DBConnectionPool dbConnectionPool;
    
    private final static int MAX_HISTORY_ITEMS = 100;
    private final static long NAP_MILLIS = 60000;
    private volatile boolean interrupted = false;
    
    
    @Data
    @Builder
    public static class TelemetryItem {
        private long totalMemory;
        private long freeMemory;
        private long maxMemory;
        private long totalDisk;
        private long freeDisk;
        private long usableDisk;
        private long timestamp;
        private int availableDbConnections;
    }
    private final List<TelemetryItem> telemetryHistory = new ArrayList<>();
    
    @PostConstruct
    void init() {
        execServiceFactory.runDaemon(TELEMETRY_THREAD_NAME, this::pollTelemetry);
    }
    
    @PreDestroy
    void shutdown() {
        interrupted = true;
    }


    private void pollTelemetry() {
        File fs = new File(".");
        while(!interrupted) {
            synchronized(this) {
                if(telemetryHistory.size() >= MAX_HISTORY_ITEMS) {
                    telemetryHistory.remove(0);
                }
                
                Runtime rt = Runtime.getRuntime();
                telemetryHistory.add(TelemetryItem.builder().//
                        totalMemory(rt.totalMemory()).
                        freeMemory(rt.freeMemory()).
                        maxMemory(rt.maxMemory()).
                        totalDisk(fs.getTotalSpace()).
                        freeDisk(fs.getFreeSpace()).
                        usableDisk(fs.getUsableSpace()).
                        timestamp(ts.getMills()).
                        availableDbConnections(dbConnectionPool.countAvailableConnections()).
                        build());
            }
            
            try {
                Thread.sleep(NAP_MILLIS);
            } catch (InterruptedException e) {}
        }
    }
    
    public synchronized List<TelemetryItem> getTelemetryHistory() { 
        return Collections.unmodifiableList(new ArrayList<>(telemetryHistory));
    }
}
