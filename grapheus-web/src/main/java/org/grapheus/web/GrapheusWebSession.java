/**
 * 
 */
package org.grapheus.web;

import java.nio.charset.Charset;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;

import lombok.Getter;

/**
 * @author black
 *
 */
public class GrapheusWebSession extends AuthenticatedWebSession {
    @Getter
    private String username;

    @Getter
    private byte[] password;
    
    private static final long serialVersionUID = 1L;


    public static final String SUPERUSER_NAME = "admin";
    private static final String SUPERUSER_PASSWORD = System.getProperty("refleion.web.admin.pwd", "bebebe");

    public GrapheusWebSession(Request request) {
        super(request);
    }

    @Override
    protected boolean authenticate(String username, String password) {
        
        this.username = username;
        this.password = password.getBytes(Charset.forName("UTF-8"));

        if(SUPERUSER_NAME.equals(username) && SUPERUSER_PASSWORD.equals(password)) {
            return true;
        }
        return RemoteUtil.userCreationAPI().checkUser(username, this.password);
    }

    @Override
    public Roles getRoles() {
        if(username.equals("superuser")) {
            new Roles(Roles.ADMIN);
        }
        return new Roles(Roles.USER);
    }

}
