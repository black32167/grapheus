/**
 * 
 */
package grapheus.server.config;

import java.net.URL;

import org.grapheus.common.config.HumanReadableConfigConsumer;
import org.grapheus.common.config.HumanReadableConfigProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.Getter;

/**
 * @author black
 */
@Service
@Getter
public final class ConcurrencyBackendConfig implements HumanReadableConfigProvider {
    private final static String EXECUTORS_CONFIG_LOCATION_KEY = "${executors.config}";
    private final static String DEFAULT_THREADS_LIMIT_KEY = "${default.threads.limit}";

    @Value(EXECUTORS_CONFIG_LOCATION_KEY) 
    private URL executorsConfigUrl;
    
    @Value(DEFAULT_THREADS_LIMIT_KEY)
    private int defaultThreadLimit;
    
	private ConcurrencyBackendConfig() {}

	@Override
	public void provideConfig(HumanReadableConfigConsumer consumer) {
		consumer.addConfig("Default threads per runner", DEFAULT_THREADS_LIMIT_KEY, defaultThreadLimit);
		consumer.addConfig("Executor config location", EXECUTORS_CONFIG_LOCATION_KEY, executorsConfigUrl);
	}
}
