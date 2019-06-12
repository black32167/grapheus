/**
 * 
 */
package grapheus.runner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.concurrent.CountDownLatch;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author black
 *
 */

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
@Slf4j
public class ShutdownLatch {
    private final int port;
    private final String stopToken;
    private final String statusToken;
    private CountDownLatch shutdownLatch = new CountDownLatch(1);
    private Thread serverThread;
    private volatile boolean initialized; 

    public void waitForShutdown() {
        initialized = true;
        try {
            shutdownLatch.await();
        } catch (InterruptedException e) {
            log.warn("Shutdown latch was interrupted");
        } 
    }
    
    @PostConstruct
    void initShutdownHook() throws IOException {
        serverThread = new Thread(this::executeServer, "Shutdown thread");
        serverThread.start();
    }
    
    @PreDestroy
    void shutdown() throws InterruptedException {
        serverThread.interrupt();
        serverThread.join();
    }
    

    private void executeServer() {
        log.info("Establishing shutdown listener on port {}", port);
        try(ServerSocket ss = new ServerSocket(port)) {
            log.info("Established shutdown listener on port {}", port);
            String command = "";
            while(!stopToken.equals(command)) {
                
                try(Socket s = ss.accept()) {
                    log.info("Got connection - reading command...");
                    try(BufferedReader bis = new BufferedReader(new InputStreamReader(s.getInputStream(), Charset.forName("UTF-8")));
                        BufferedWriter bos = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {
                        command = bis.readLine();
                        log.info("Read administrative command: {}", command);
                  
                        if(statusToken.equals(command)) {
                            bos.write(initialized ? "started" : "starting");
                        }
                    }
      
                }
                
                    
            }
            log.info("Got shutdown command");
            shutdownLatch.countDown();
        } catch (Exception e) {
            log.error("", e);
        }
    }
    
}
