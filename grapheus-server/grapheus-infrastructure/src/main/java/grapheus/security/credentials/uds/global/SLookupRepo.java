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
public class SLookupRepo {
    private String sourceId;
    private String repoAccount;
    private List<String> repoNames;
}
