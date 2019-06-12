/**
 * 
 */
package org.grapheus.client.model.graph.generate;

import org.grapheus.client.model.graph.edge.EdgeDirection;

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
@AllArgsConstructor
@NoArgsConstructor
public class RGraphCreationParameters {
    private String newGraphName;
    private String sourceProperty; // for GeneratorType.PROPERTY
    private String startingVertex;
    private EdgeDirection traversalDirection;
    
    public static RGraphCreationParameters propertyBased(
            String targetGraphName, String property) {
        return RGraphCreationParameters.builder().
                newGraphName(targetGraphName).
                sourceProperty(property).
                build();
    }

    public static RGraphCreationParameters cyclic(
            String targetGraphName) {
        return RGraphCreationParameters.builder().
                newGraphName(targetGraphName).
                build();
    }

    public static RGraphCreationParameters clone(String targetGraphName) {
        return RGraphCreationParameters.builder().
                newGraphName(targetGraphName).
                build();
    }
    public static RGraphCreationParameters empty(String newGraphName) {
        return RGraphCreationParameters.builder().
                newGraphName(newGraphName).
                build();
    }

    public static RGraphCreationParameters self(String newGraphName) {
        return RGraphCreationParameters.builder().
                newGraphName(newGraphName).
                build();
    }

    public static RGraphCreationParameters traverse(
             String newGraphName, String startVertexId, EdgeDirection traversalDirection) {
        return RGraphCreationParameters.builder().
                newGraphName(newGraphName).
                startingVertex(startVertexId).
                traversalDirection(traversalDirection).
                build();
    }

}
