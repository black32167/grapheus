/**
 * 
 */
package org.grapheus.client.model.graph.operation;

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
public class RVerticesRemoveOperationContainer {
    private String graphId;
    private Collection<String> verticesIds;
}
