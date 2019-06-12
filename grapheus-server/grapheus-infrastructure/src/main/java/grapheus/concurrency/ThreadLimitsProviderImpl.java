/**
 * 
 */
package grapheus.concurrency;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import grapheus.server.config.ConcurrencyBackendConfig;

/**
 * @author black
 */
@Service
public class ThreadLimitsProviderImpl implements ThreadLimitsProvider {
    private final URL executorsConfigURL;
    private final int defaultThreadLimit;
    
    @Inject
    public ThreadLimitsProviderImpl(ConcurrencyBackendConfig config) {
        this.executorsConfigURL = config.getExecutorsConfigUrl();
        this.defaultThreadLimit = config.getDefaultThreadLimit();
    }
    
    private final Map<String, Integer> threadLimits = new HashMap<>();
    
    @PostConstruct
    public void init() throws IOException {
        InputStream is = executorsConfigURL.openStream();
        if(is != null) {
            IOUtils.readLines(is, "UTF-8").stream().//
                map(l -> l.split("=")).//
                forEach(c -> threadLimits.put(c[0], Integer.parseInt(c[1])));
        }
    }
    /**
     * @see grapheus.concurrency.ThreadLimitsProvider#getMaxThreads(java.lang.String)
     */
    @Override
    public int getMaxThreads(String runnerName) {
        return threadLimits.computeIfAbsent(runnerName, key -> Integer.getInteger(runnerName, defaultThreadLimit));
    }

}
