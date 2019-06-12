/**
 * 
 */
package org.grapheus.web;

import org.grapheus.common.config.ConfigurationAccumulator;
import org.grapheus.common.config.HumanReadableConfigConsumer;
import org.grapheus.common.config.GrapheusStaticConfiguration;

import lombok.extern.slf4j.Slf4j;

import static org.grapheus.web.WebappConfigKeys.KEY_BASE_URL;
import static org.grapheus.web.WebappConfigKeys.KEY_PORT;
import static org.grapheus.web.WebappConfigKeys.KEY_BACKEND_URL;

/**
 * Static settings class
 * 
 * @author black
 */
@Slf4j
public class WebappSettings {
    public static int getPort() {
    	return GrapheusStaticConfiguration.getInteger(KEY_PORT, ()->8000);
    }
    
    public static String getBaseUrl() {
    	return GrapheusStaticConfiguration.getString(KEY_BASE_URL, ()->String.format("http://localhost:%s", getPort()));
    }
    
    public static String getBackendUrl() {
    	return GrapheusStaticConfiguration.getString(KEY_BACKEND_URL, ()->"http://127.0.0.1:8081/grapheus");
    }

	public static void logSettings() {
		HumanReadableConfigConsumer configAccumulator = new ConfigurationAccumulator()
			.addConfig("Base web URL", KEY_BASE_URL, getBaseUrl())
			.addConfig("Backend URL", KEY_BACKEND_URL, getBackendUrl());
		
		log.info("Effective configuration:\n{}", configAccumulator);
		
	}
}
