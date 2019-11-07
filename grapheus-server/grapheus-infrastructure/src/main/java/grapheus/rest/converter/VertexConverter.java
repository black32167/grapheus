/**
 * 
 */
package grapheus.rest.converter;

import grapheus.persistence.model.graph.PersistentVertex;
import grapheus.view.SemanticFeature;
import org.grapheus.client.model.graph.vertex.RVertex;
import org.grapheus.client.model.graph.vertex.RVertex.RProperty;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

/**
 * @author black
 */
public final class VertexConverter {

    public static RVertex toExternalVertex(PersistentVertex internalVertexModel) {
        List<RProperty> properties = toExternalProperties(internalVertexModel.getSemanticFeatures());
        return RVertex.builder().//
                description(internalVertexModel.getDescription()).//
                title(internalVertexModel.getTitle()).//
                id(internalVertexModel.getId()).//
                updateTimeMills(Optional.ofNullable(internalVertexModel.getUpdatedTimestamp()).orElse(0L)).//
                properties(properties).//
                tags(ofNullable(internalVertexModel.getTags()).orElse(emptyList())).//
                build();
    }

    public static PersistentVertex toInternal(RVertex remoteVertexModel) {
        return PersistentVertex.builder().//
                url(null).
                id(ofNullable(remoteVertexModel.getId()).orElseGet(() -> UUID.randomUUID().toString())).//
                semanticFeatures(toInternalProperties(remoteVertexModel.getProperties())).
                description(remoteVertexModel.getDescription()).//
                title(remoteVertexModel.getTitle()).//
                tags(ofNullable(remoteVertexModel.getTags()).orElse(emptyList())).//
                build();
    }

    private static Map<String, SemanticFeature> toInternalProperties(List<RProperty> properties) {
        if(properties == null) {
            return new HashMap<>();//!!! Important to be mutable
        }
        return properties.stream().//
                map(extProp -> SemanticFeature.builder().//
                        feature(extProp.getName()).//
                        value(extProp.getValue()).//
                        build()).//
                collect(Collectors.toMap(SemanticFeature::getFeature, Function.identity()));
    }


    private static List<RProperty> toExternalProperties(Map<String, SemanticFeature> semanticFeatures) {
        if(semanticFeatures == null) {
            return Collections.emptyList();
        }
        return semanticFeatures.values().stream()
                .map(f-> RProperty.builder().name(f.getFeature()).value(f.getValue()).build())
                .collect(Collectors.toList());
    }
}
