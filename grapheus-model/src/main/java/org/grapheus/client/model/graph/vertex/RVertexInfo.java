/**
 * 
 */
package org.grapheus.client.model.graph.vertex;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author black
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RVertexInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String vertexKey;
    private String infoData;
}
