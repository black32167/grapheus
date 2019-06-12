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
public class SOAuthCredentials {
    private String appKey;
    private String appSecret;
    private String accessTokenURL;
    private String redirectURL;
    private String authorizationURL;
}
