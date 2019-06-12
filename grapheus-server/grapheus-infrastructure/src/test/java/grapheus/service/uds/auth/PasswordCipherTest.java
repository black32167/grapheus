/**
 * 
 */
package grapheus.service.uds.auth;

import org.junit.Assert;
import org.junit.Test;

import grapheus.security.credentials.uds.PasswordCipher;

/**
 * @author black
 *
 */
public class PasswordCipherTest {
    //PasswordCipher should not have state
    private PasswordCipher cipher = new PasswordCipher();
    
    @Test
    public void testEncodingDecoding() {
        byte[] key = new byte[]{1};
        byte[] password = "12345".getBytes();
        
        byte[] encoded = cipher.encodePassword(key, password);
        byte[] decoded = cipher.decodePassword(key, encoded);
        
        Assert.assertArrayEquals("Encoded should match decoded", password, decoded);
    }
}
