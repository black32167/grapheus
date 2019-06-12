/**
 * 
 */
package org.grapheus.web;

import org.grapheus.client.GrapheusClientFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * @author black
 *
 */
@Slf4j
public final class GrapheusClientFactoryHolder {
    private final static String baseURL = WebappSettings.getBackendUrl();
    
    static final class Holder {
        public static GrapheusClientFactory INSTANCE = new GrapheusClientFactory(baseURL);
    }
    
    public static GrapheusClientFactory grapheusFactory() {
        return Holder.INSTANCE;
    }
    
    private GrapheusClientFactoryHolder(){}
}
