/**
 * 
 */
package org.grapheus.client.model.graph.vertex;

import lombok.*;

import java.util.List;

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
        private String name;
        private String value;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class RReference {
        private List<String> classifiers;
        private String destinationId;
    }

    private String localId;//local id //TODO: remove
    private String sourceUrl;
    private String artifactId;//unique id //TODO: rename to 'id'
    private String title;
    private String description;
    private long updateTimeMills; //TODO: convert to 'Long'?

    @Singular
    private List<RProperty> properties;

    @Singular
    private List<RReference> references; // TODO: add support <<<<<<<<<<

}
