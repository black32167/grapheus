/**
 * 
 */
package org.grapheus.client.model.graph.vertex;

import java.io.Serializable;
import java.util.Collection;

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
public class RVerticesContainer implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean editPermitted;
    private Collection<RVertex> artifacts;
    private long totalCount;
}
