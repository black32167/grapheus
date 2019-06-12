/**
 * 
 */
package org.grapheus.client.model.graph.search;

import java.util.List;

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

}
