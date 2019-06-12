/**
 * 
 */
package grapheus.view.extract.features;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.NonNull;
import grapheus.persistence.model.graph.PersistentVertex;
import grapheus.view.SemanticFeature;

/**
 * @author black
 */
public final class HintsUtils {
    public static List<SemanticFeature> toHints(String featureType, Collection<String> values) {
        return values.stream().map(v->SemanticFeature.builder().feature(featureType).value(v).build()).collect(Collectors.toList());
    }
    
    public static Set<String> getFeatureValues(
            Collection<PersistentVertex> unprocessedArtifactChunk,
            @NonNull String... featureTypes) {
        List<String> featureTypesList = Arrays.asList(featureTypes);
        return unprocessedArtifactChunk.stream().//
            flatMap(u -> u.getSemanticFeatures().stream()).//
            filter(f->featureTypesList.contains(f.getFeature())).//
            map(f->f.getValue()).//
            collect(Collectors.toSet());
    }

    public static Set<String> getExternalIds(Collection<PersistentVertex> artifacts) {
        return artifacts.stream().map(a -> a.getExternalCompositeId()).collect(Collectors.toSet());
    }

    private HintsUtils() {}

    
}
