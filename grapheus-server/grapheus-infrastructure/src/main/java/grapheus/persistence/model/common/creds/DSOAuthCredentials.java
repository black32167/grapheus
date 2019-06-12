/**
 * 
 */
package grapheus.persistence.model.common.creds;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;
import grapheus.security.credentials.uds.oauth.AccessTokenInfo;

/**
 * @author black
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Wither
public class DSOAuthCredentials implements DSCredentials {
    private AccessTokenInfo accessTokenInfo;
    private long accessTokenTimestamp;
    private String clientId;
    private byte[] clientSecret;
}
