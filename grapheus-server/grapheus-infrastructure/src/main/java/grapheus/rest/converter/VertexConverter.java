/**
 * 
 */
package grapheus.rest.converter;

import grapheus.persistence.model.graph.PersistentVertex;
import grapheus.view.SemanticFeature;
import org.grapheus.client.model.graph.vertex.RVertex;
import org.grapheus.client.model.graph.vertex.RVertex.RProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author black
 *
 */
public final class VertexConverter {

    public static RVertex toExternalVertex(PersistentVertex internalVertexModel) {
        return RVertex.builder().//
                description(internalVertexModel.getDescription()).//
                title(internalVertexModel.getTitle()).//
                id(internalVertexModel.getId()).//
                updateTimeMills(internalVertexModel.getUpdatedTimestamp()).//
                build();
    }

    public static PersistentVertex toInternal(RVertex remoteVertexModel) {

        return PersistentVertex.builder().//
                url(null).
                id(Optional.ofNullable(remoteVertexModel.getId()).orElseGet(() -> UUID.randomUUID().toString())).//
                semanticFeatures(toInternalProperties(remoteVertexModel.getProperties())).
                description(remoteVertexModel.getDescription()).//
                title(remoteVertexModel.getTitle()).//
                build();
    }

    private static List<SemanticFeature> toInternalProperties(List<RProperty> properties) {
        if(properties == null) {
            return new ArrayList<>();//!!! Important to be mutable
        }
        return properties.stream().//
                map(extProp -> SemanticFeature.builder().//
                        feature(extProp.getName()).//
                        value(extProp.getValue()).//
                        build()).//
                collect(Collectors.toList());
    }

}
