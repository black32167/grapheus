/**
 * 
 */
package org.grapheus.client.model.graph.vertex;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author black
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RVertex {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class RProperty {
        String name;
        String value;
    }

    private String localId;//local datasource id
    private String sourceUrl;
    private String artifactId;//unique id
    private String title;
    private String description;
    private long updateTimeMills;
    private RProperty[] properties;

}
