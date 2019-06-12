/**
 * 
 */
package org.grapheus.client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author black
 *
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RHealthcheckResponse {
    private boolean ready;
}
