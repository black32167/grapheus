package org.grapheus.client.model.graph.generate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RCollapsedGraphParameters {
    private String newGraphId;
    private String groupingProperty;
}
