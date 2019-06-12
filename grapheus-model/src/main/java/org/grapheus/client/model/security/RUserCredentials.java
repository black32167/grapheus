/**
 * 
 */
package org.grapheus.client.model.security;

import java.io.Serializable;

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
public class RUserCredentials implements Serializable {

    private static final long serialVersionUID = 1L;
    private byte[] userSecret;
}
