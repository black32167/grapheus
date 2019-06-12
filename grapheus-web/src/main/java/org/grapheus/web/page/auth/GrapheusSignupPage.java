
package org.grapheus.web.page.auth;

import java.nio.charset.Charset;

import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.grapheus.web.RemoteUtil;
import org.grapheus.web.page.base.AbstractGrapheusPage;

/**
 * @author black
 *
 */
public class GrapheusSignupPage extends AbstractGrapheusPage {

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
                try {
                    RemoteUtil.userCreationAPI().createUser(username, password.getBytes(Charset.forName("UTF-8")));
                    setResponsePage(getApplication().getHomePage());
                } catch (Exception e) {
                    error("Remote server error:" + e.getMessage());
                }
            }
        };

        form.setDefaultModel(new CompoundPropertyModel<GrapheusSignupPage>(this));

        form.add(new TextField<String>("username"));
        form.add(new PasswordTextField("password"));

        add(form);
    }
}
