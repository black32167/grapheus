
package org.grapheus.web.page.auth;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.util.string.Strings;
import org.grapheus.web.GrapheusWebSession;
import org.grapheus.web.page.base.AbstractGrapheusPage;
import org.grapheus.web.telemetry.GrapheusTelemetryPage;

/**
 * @author black
 *
 */
public class GrapheusSigninPage extends AbstractGrapheusPage {

    private static final long serialVersionUID = 1L;
    private String username;
    private String password;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new FeedbackPanel("feedback"));
        StatelessForm<Void> form = new StatelessForm<Void>("form") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit() {
                if (Strings.isEmpty(username)) {
                    return;
                }

                boolean authResult = AuthenticatedWebSession.get().signIn(username, password);
                // if authentication succeeds redirect user to the requested
                // page
                if (authResult) {
                    if(GrapheusWebSession.SUPERUSER_NAME.equals(username)) {
                        setResponsePage(GrapheusTelemetryPage.class);
                    } else {
                        continueToOriginalDestination();
                        setResponsePage(getApplication().getHomePage());
                    }
                } else {
                    error("Wrong username or password");
                }
            }
        };
        
        form.setDefaultModel(new CompoundPropertyModel<GrapheusSigninPage>(this));

        form.add(new BookmarkablePageLink<>("signUp", GrapheusSignupPage.class));
        form.add(new TextField<String>("username"));
        form.add(new PasswordTextField("password"));

        add(form);
    }
}
