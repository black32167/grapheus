/**
 * 
 */
package org.grapheus.web;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.apache.wicket.protocol.http.WicketFilter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.grapheus.web.servlet.HealthcheckServlet;

import lombok.extern.slf4j.Slf4j;

/**
 * @author black
 *
 */
@Slf4j
public class GrapheusWebAppRunner {
    public static void main(String[] args) throws Exception {
        
        log.info("Starting Grapheus - web");

        Server server = new Server(WebappSettings.getPort());
        
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        FilterHolder wicketFilter = context.addFilter(WicketFilter.class, "/app/*", EnumSet.of(DispatcherType.REQUEST));
        wicketFilter.setInitParameter("applicationClassName", GrapheusWebApp.class.getName());
        wicketFilter.setInitParameter(WicketFilter.FILTER_MAPPING_PARAM, "/app/*");
        context.addServlet(HealthcheckServlet.class, "/healthcheck");
        server.setHandler(context);
        

        server.start();
		log.info("Application is started, you can navigate {} in your browser.", WebappSettings.getBaseUrl()+"/app");
		WebappSettings.logSettings();
        
        server.join();
    }
}
