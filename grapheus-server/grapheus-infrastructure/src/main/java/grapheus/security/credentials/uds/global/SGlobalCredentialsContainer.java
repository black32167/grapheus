/**
 * 
 */
package grapheus.security.credentials.uds.global;

import java.util.List;

import lombok.Data;

/**
 * @author black
 *
 */
@Data
public class SGlobalCredentialsContainer {
    private List<SGlobalCredentials> credentials;
    private List<SLookupRepo> lookupRepos;
}
