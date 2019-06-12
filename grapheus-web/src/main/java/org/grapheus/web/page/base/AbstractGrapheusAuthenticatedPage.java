/**
 * 
 */
package org.grapheus.web.page.base;

import java.util.Collections;
import java.util.List;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.grapheus.web.AuthUtil;

import com.googlecode.wicket.jquery.ui.plugins.sfmenu.ISfMenuItem;
import com.googlecode.wicket.jquery.ui.plugins.sfmenu.SfMenu;

/**
 * @author black
 */
abstract public class AbstractGrapheusAuthenticatedPage extends AbstractGrapheusPage {
    private static final long serialVersionUID = 1L;

    public AbstractGrapheusAuthenticatedPage(PageParameters parameters) {
        super(parameters);
    }
    
    public AbstractGrapheusAuthenticatedPage() {
      
    }
    

    @Override
    protected void onInitialize() {
        super.onInitialize();
        
        AuthenticatedWebApplication app = (AuthenticatedWebApplication) Application.get();
        // if user is not signed in, redirect him to sign in page
        if (!AuthenticatedWebSession.get().isSignedIn()) {
            app.restartResponseAtSignInPage();
        }
        
        add(new BookmarkablePageLink<>("home", getApplication().getHomePage()));
        add(new Link("logOut") {
            @Override
            public void onClick() {
                AuthenticatedWebSession.get().invalidate();
                setResponsePage(getApplication().getHomePage());
            }
        }.add(new Label("loggedUser", AuthUtil.getUserName())));
        
        add(newMenu("menu"));
        
        
    }

    protected Component newMenu(String id) {

        List<ISfMenuItem> menuItems = getMenuItems();
        return new SfMenu(id, menuItems);
    }

    protected List<ISfMenuItem> getMenuItems() {
        return Collections.emptyList();
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
       
    }

}
