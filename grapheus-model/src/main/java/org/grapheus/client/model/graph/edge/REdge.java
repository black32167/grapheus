/**
 * 
 */
package org.grapheus.client.model.graph.edge;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author black
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class REdge {
    private String from;
    private String to;

    private List<String> tags;
}
