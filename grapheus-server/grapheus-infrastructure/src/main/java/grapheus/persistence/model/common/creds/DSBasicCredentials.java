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
@AllArgsConstructor
@NoArgsConstructor
public class DSBasicCredentials implements DSCredentials {
	private String userName;
	
	private byte[] userPassword;
}
