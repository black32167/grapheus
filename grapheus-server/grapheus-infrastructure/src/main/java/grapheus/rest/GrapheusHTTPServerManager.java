/**
 * 
 */
package grapheus.rest;

import javax.inject.Inject;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import grapheus.server.common.AbstractHTTPServerManager;
import grapheus.server.common.BaseURIProvider;

/**
 * @author black
 */
@Service
public class GrapheusHTTPServerManager extends AbstractHTTPServerManager {

    @Inject
    public GrapheusHTTPServerManager(ApplicationContext ctx, BaseURIProvider baseURIProvider) {
        super(ctx, baseURIProvider);
    }
    
}
