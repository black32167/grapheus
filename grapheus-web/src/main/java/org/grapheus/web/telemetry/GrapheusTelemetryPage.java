/**
 * 
 */
package org.grapheus.web.telemetry;

import java.util.List;

import org.apache.wicket.Application;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.grapheus.client.model.telemetry.RServerTelemetryContainer;
import org.grapheus.client.model.telemetry.RTelemetry;
import org.grapheus.web.RemoteUtil;
import org.grapheus.web.page.base.AbstractGrapheusPage;

/**
 * @author black
 *
 */
//@AuthorizeInstantiation("ADMIN")
public class GrapheusTelemetryPage extends AbstractGrapheusPage {
   

    private static final long serialVersionUID = 1L;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        
        add(new Link("logOut") {
            @Override
            public void onClick() {
                AuthenticatedWebSession.get().invalidate();
                setResponsePage(getApplication().getHomePage());
            }
        });
        
        add(new ListView<RServerTelemetryContainer>("servers", serversTelemetryModel()) {

            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<RServerTelemetryContainer> serverItem) {
                RServerTelemetryContainer serverTelemetry = serverItem.getModelObject();
                serverItem.add(new Label("serverId", serverTelemetry.getServerId()));
                
                serverItem.add(new ListView<RTelemetry>("telemetryHistory", serverTelemetry.getTelemetryHistory()) {
                    private static final long serialVersionUID = 1L;
                    @Override
                    protected void populateItem(ListItem<RTelemetry> item) {
                        RTelemetry telemetryItem = item.getModelObject();
                        item.add(new Label("totalMemory", telemetryItem.getTotalMemory()));
                        item.add(new Label("freeMemory", telemetryItem.getFreeMemory()));
                        item.add(new Label("maxMemory", telemetryItem.getMaxMemory()));
                        item.add(new Label("totalDisk", telemetryItem.getTotalDisk()));
                        item.add(new Label("freeDisk", telemetryItem.getFreeDisk()));
                        item.add(new Label("usableDisk", telemetryItem.getUsableDisk()));
                        item.add(new Label("timestamp", telemetryItem.getTimestamp()));
                    }
                });
            }
        });
    }

    private IModel<? extends List<RServerTelemetryContainer>> serversTelemetryModel() {
        return new AbstractReadOnlyModel<List<RServerTelemetryContainer>>() {
            private static final long serialVersionUID = 1L;
            @Override
            public List<RServerTelemetryContainer> getObject() {
                return RemoteUtil.telemetryAPI().getTelemetry().getServersTelemetry();
            }
        };
    }
    
    @Override
    public void renderHead(IHeaderResponse response) {

        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(GrapheusTelemetryPage.class,
                "js/jquery.min.js")));
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(GrapheusTelemetryPage.class,
                "js/jcanvas.min.js")));
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(GrapheusTelemetryPage.class,
                "js/jquery.canvasjs.min.js")));
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(GrapheusTelemetryPage.class,
                "js/tele-graph.js")));

        super.renderHead(response);
    }
    

    @Override
    protected void onConfigure() {
        super.onConfigure();
        AuthenticatedWebApplication app = (AuthenticatedWebApplication) Application.get();
        // if user is not signed in, redirect him to sign in page
        if (!AuthenticatedWebSession.get().isSignedIn())
            app.restartResponseAtSignInPage();
    }

}
