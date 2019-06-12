/**
 * 
 */
package grapheus.persistence.model.common.creds;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author black
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DSJWTCredentials implements DSCredentials {
    private String sharedSecret;
}
