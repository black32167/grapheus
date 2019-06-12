/**
 * 
 */
package org.grapheus.client.model;

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
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RError implements Serializable {
    private static final long serialVersionUID = 1L;
    private String errorDescription;
}
