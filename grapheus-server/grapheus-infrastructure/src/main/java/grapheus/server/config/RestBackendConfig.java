/**
 * 
 */
package grapheus.server.config;

import org.grapheus.common.config.HumanReadableConfigConsumer;
import org.grapheus.common.config.HumanReadableConfigProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.Getter;

/**
 * @author black
 */
@Getter
@Service
public class RestBackendConfig implements HumanReadableConfigProvider {
	private static final String GRAPHEUS_BASE_URI_KEY = "${grapheus.base.uri.key}";

	@Value(GRAPHEUS_BASE_URI_KEY)
	private String baseURI;

	@Override
	public void provideConfig(HumanReadableConfigConsumer consumer) {
		consumer.addConfig("Grapheus server base url", GRAPHEUS_BASE_URI_KEY, baseURI);
	}

}
