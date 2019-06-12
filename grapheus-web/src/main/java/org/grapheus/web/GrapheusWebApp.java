package org.grapheus.web;

import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.core.request.handler.RenderPageRequestHandler;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.eclipse.jetty.http.HttpStatus;
import org.grapheus.client.http.ServerErrorResponseException;
import org.grapheus.web.page.auth.GrapheusSigninPage;
import org.grapheus.web.page.vertices.list.VerticesPage;

import lombok.extern.slf4j.Slf4j;

/**
 * Hello world!
 *
 */
@Slf4j
public class GrapheusWebApp extends AuthenticatedWebApplication {
    
    /**
     * @see org.apache.wicket.Application#getHomePage()
     */
    @Override
    public Class<? extends WebPage> getHomePage()
    {
        return VerticesPage.class;
    }

    /**
     * @see org.apache.wicket.Application#init()
     */
    @Override
    public void init()
    {
        super.init();
        
        this.getMarkupSettings().setStripWicketTags(true);
        
        this.getRequestCycleListeners().add(new AbstractRequestCycleListener() {
            @Override
            public IRequestHandler onException(RequestCycle cycle, Exception ex) {
                Throwable cause = ex;
                while(cause != null) {
                    if(cause instanceof ServerErrorResponseException) {
                        ServerErrorResponseException serverErrorException = (ServerErrorResponseException) cause;
                        if(serverErrorException.getCode() == HttpStatus.UNAUTHORIZED_401) {
                            return new RenderPageRequestHandler(new PageProvider(GrapheusSigninPage.class));
                        }
                    }
                    cause = cause.getCause();
                }
               
                return cycle.getActiveRequestHandler();
            }

            @Override
            public void onEndRequest(RequestCycle cycle) {
                log.debug("Session size = {} bytes", Session.get().getSizeInBytes());
            }
            
        });
    }

    @Override
    protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
        return GrapheusWebSession.class;
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return GrapheusSigninPage.class;
    }
}

