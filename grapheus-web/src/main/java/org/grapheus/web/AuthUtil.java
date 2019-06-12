package org.grapheus.web;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;

public final class AuthUtil {
    public static String getUserName() {
        return ((GrapheusWebSession)AuthenticatedWebSession.get()).getUsername();
    }

    public static byte[] getPassword() {
        return ((GrapheusWebSession)AuthenticatedWebSession.get()).getPassword();
    }
}
