/**
 * 
 */
package grapheus.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author black
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class SemanticFeature {
    public static final String FEATURE = "feature";
    public static final String VALUE = "value";
    private String feature;
    private String value;
    private int intValue;

    public static SemanticFeature from(String type, String value) {
        return SemanticFeature.builder().//
                feature(type).//
                value(value).
                build();
    }

    public static SemanticFeature from(String type, int value) {
        return SemanticFeature.builder().//
                feature(type).//
                intValue(value).
                build();
    }

    public static Map<String, SemanticFeature> toMap(List<SemanticFeature> features) {
        return features.stream().collect(Collectors.toMap(SemanticFeature::getFeature, Function.identity()));
    }
}
