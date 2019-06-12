/**
 * 
 */
package grapheus.server.common;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.grapheus.common.rest.GsonContextResolver;
import org.springframework.context.ApplicationContext;
import org.zalando.jersey.gson.GsonFeature;

import lombok.RequiredArgsConstructor;


/**
 * @author black
 *
 */
@RequiredArgsConstructor
public class AbstractHTTPServerManager {
    private static final String SCAN_PACKAGE_ROOT = "grapheus";
    private final ApplicationContext ctx;
//    private final GrapheusAuthFilter authFilter;
    private final BaseURIProvider baseURIProvider;
    
    private HttpServer httpServer;

    @PostConstruct
    void init() {
        ResourceConfig config = getConfig();
        
        httpServer = GrizzlyHttpServerFactory.createHttpServer(baseURIProvider.getBaseURI(), config);
    }
    
    protected ResourceConfig getConfig() {
       
        return new ResourceConfig()//
//              .register(GenericExceptionMapper.class)//
//              .register(authFilter)
              .register(GsonContextResolver.class)//
              .register(GsonFeature.class)//
              .register(LoggingFeature.class)//
              .packages(SCAN_PACKAGE_ROOT)
              .property("contextConfig", ctx);//  
    }

    @PreDestroy
    void shutdown() {
        httpServer.shutdownNow();
    }
}
