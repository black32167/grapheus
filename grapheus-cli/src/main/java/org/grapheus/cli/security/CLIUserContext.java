/**
 * 
 */
package org.grapheus.cli.security;

import java.util.function.Supplier;

import org.grapheus.client.http.auth.GrapheusClientCredentials;

//import java.nio.CharBuffer;
//import java.nio.charset.StandardCharsets;

import org.grapheus.common.BasicAuthUtil;
import org.springframework.stereotype.Service;

/**
 * @author black
 *
 */
@Service
public class CLIUserContext {
    private static final String GRAPHEUS_NAME_KEY = "grapheus_name";
    private static final String GRAPHEUS_PASS_KEY = "grapheus_password_base64";

    public Supplier<GrapheusClientCredentials> credentialsSupplier() {
        return () -> new GrapheusClientCredentials(
                getUserName(), getPassword());
    }
    
    public String getUserName() {
        String userName = System.getenv(GRAPHEUS_NAME_KEY);

        return userName;
    }
    
    public byte[] getPassword() {
        String sPassword = System.getenv(GRAPHEUS_PASS_KEY);

        return BasicAuthUtil.fromBase64(sPassword);
    }
}
