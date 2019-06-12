/**
 * 
 */
package grapheus.absorb.match;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import grapheus.view.SemanticFeature;

/**
 * @author black
 *
 */
public final class FeatureUtils {
    public static List<String> getValues(String featureType, Collection<SemanticFeature> features){
        return features.stream().//
                filter(f->Objects.equals(featureType, f.getFeature())).//
                map(f->f.getValue()).//
                collect(Collectors.toList());
    }
    
    private FeatureUtils() {}

}
