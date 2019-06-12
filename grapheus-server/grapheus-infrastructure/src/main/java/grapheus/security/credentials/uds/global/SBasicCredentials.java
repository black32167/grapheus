/**
 * 
 */
package grapheus.security.credentials.uds.global;

import lombok.Data;

/**
 * @author black
 *
 */
@Data
public class SBasicCredentials {
    private String user;
    private byte[] password;
}
