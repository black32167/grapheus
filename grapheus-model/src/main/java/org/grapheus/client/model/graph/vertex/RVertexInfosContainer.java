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
public class RVertexInfosContainer implements Serializable {
    private static final long serialVersionUID = 1L;
    private Collection<RVertexInfo> infos;

}
