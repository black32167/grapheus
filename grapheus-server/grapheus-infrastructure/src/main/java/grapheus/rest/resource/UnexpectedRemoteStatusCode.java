/**
 * 
 */
package grapheus.rest.resource;

import javax.ws.rs.core.Response;

import lombok.Getter;

/**
 * @author black
 *
 */
public class UnexpectedRemoteStatusCode extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    @Getter
    private int status;

    @Getter
    private String uri;
    
    public UnexpectedRemoteStatusCode(Response response, String uri) {
        super(String.format("Unexpected response code %s from '%s'",
                response.getStatus(), uri));
        this.status = response.getStatus();
        this.uri = uri;

    }
    

}
