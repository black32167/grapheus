/**
 * 
 */
package org.grapheus.client.model.graph.search;

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
@NoArgsConstructor
@AllArgsConstructor
public class RSearchRequest {
    private String minEdgesSpec;
    private String scope;
    private String filteringUser;
    private Boolean sinks;
    private String title;
    private List<String> verticesIds;
    private String sortingCriteriaSpec;
    private RVertexPropertyFilter vertexPropertyFilter;

}
