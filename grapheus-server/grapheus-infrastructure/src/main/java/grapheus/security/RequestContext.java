/**
 * 
 */
package grapheus.security;

import java.util.ArrayList;
import java.util.List;

import org.grapheus.client.model.RError;

import lombok.Builder;
import lombok.Data;

/**
 * @author black
 *
 */
@Data
@Builder
public class RequestContext {
    private String userId;
    private byte[] userSecret;
    private String requestUrl;
    private List<RError> requestErrors;
    
    public static class RequestContextBuilder {
        private List<RError> requestErrors = new ArrayList<>();
    }
}
