/**
 * 
 */
package org.grapheus.client.model.graph.generate;

import java.util.Collection;

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
public class RPathGraphParameters {
    private String sourceGraphName;
    private String newGraphName;
    private Collection<String> boundaryVerticesIds;
}
