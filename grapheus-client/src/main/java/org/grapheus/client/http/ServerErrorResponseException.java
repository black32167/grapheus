/**
 * 
 */
package org.grapheus.client.http;

import lombok.Getter;

/**
 * @author black
 *
 */
public class ServerErrorResponseException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    @Getter
    private int code;

    public ServerErrorResponseException(String message, int code) {
        super(message + "(code " + code + ")");
        this.code = code;
    }
    public ServerErrorResponseException(int code) {
        this("Unexpected return code:" + code, code);
    }

}
