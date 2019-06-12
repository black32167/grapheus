/**
 * 
 */
package org.grapheus.client.http.auth;

import lombok.Value;

/**
 * @author black
 *
 */
@Value
public class GrapheusClientCredentials {
    private String userName;
    private byte[] secret;
}
