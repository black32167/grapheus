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
public class SGlobalCredentials {
    private String url;
    private String name;
    private String type;
    private SBasicCredentials basic;
    private SOAuthCredentials oauth;
}
