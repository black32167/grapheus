/**
 * 
 */
package org.grapheus.client.model.graph.vertex;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

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
        @Singular
        private List<String> tags;
        private boolean reversed;
        private String destinationId;
    }

    private String id;
    private String title;
    private String description;
    private long updateTimeMills; //TODO: convert to 'Long'?

    @Singular
    private List<RProperty> properties;

    @Singular
    private List<RReference> references;

    @Singular
    private List<String> tags;
}
