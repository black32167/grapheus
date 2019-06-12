/**
 * 
 */
package org.grapheus.common;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import lombok.NonNull;

/**
 * @author black
 *
 */
public class BasicAuthUtil {

    public static final Charset BASIC_ENCODING = StandardCharsets.ISO_8859_1;
    public static final String BASIC_PREFIX = "Basic ";


    public static String basicToken(@NonNull String userName, @NonNull byte[] password) {
        byte[] prefix = (userName + ":").getBytes(BASIC_ENCODING);
        byte[] encodedAuth = new byte[prefix.length + password.length];

        System.arraycopy(prefix, 0, encodedAuth, 0, prefix.length);
        System.arraycopy(password, 0, encodedAuth, prefix.length, password.length);

        return BASIC_PREFIX + Base64.getEncoder().encodeToString(encodedAuth);
    }
    
    public static String basicToken(String userName, String password) {
        return basicToken(userName, password.getBytes(BASIC_ENCODING));
    }

    public static String[] decodeAuthToken(String authTokenBase64) {
        byte[] authTokenBytes = fromBase64(authTokenBase64.substring(BASIC_PREFIX.length()));
        
        String authToken = new String(authTokenBytes, BASIC_ENCODING);
        
        String[] authPair = authToken.split(":");

        return authPair;
    }   
    
    public static byte[] fromBase64(String encoded) {
        if (encoded == null) {
            return null;
        }
        return Base64.getDecoder().decode(
                encoded.getBytes(BASIC_ENCODING));
    }
    
 
}
