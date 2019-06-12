/**
 * 
 */
package org.grapheus.client.model.graph;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * @author black
 */
@RequiredArgsConstructor
@Data
public class VerticesSortCriteria {
    private final static String INDIVIDUAL_CRITERIA_DIVIDER = ",";
    private final static String FIELDNAME_DIRECTION_DIVIDER = "_";
    
    private final VerticesSortCriteriaType sortingType;
    private final SortDirection sortDirection;

    /**
     * Format: 'sortFieldName1_{ASC|DESC}[,sortFieldName2_{ASC|DESC},...]'
     */
    public static String serializeSortingCriteria(@NonNull VerticesSortCriteria... sortingCriteria) {
        return Arrays.asList(sortingCriteria).stream().//
                map(VerticesSortCriteria::serializeSortingCriteriaItem).//
                collect(Collectors.joining(INDIVIDUAL_CRITERIA_DIVIDER));
    }
    

    /**
     * Format: 'sortFieldName1_{ASC|DESC}[,sortFieldName2_{ASC|DESC},...]'
     */
    public static List<VerticesSortCriteria> deserializeSortingCriteria(String sortingCriteriaSpec) {
        if(sortingCriteriaSpec == null) {
            return Collections.emptyList();
        }
        String[] individuallSpecs = sortingCriteriaSpec.split(INDIVIDUAL_CRITERIA_DIVIDER);
        return Arrays.asList(individuallSpecs).stream().map(VerticesSortCriteria::toSortCriteria).collect(Collectors.toList());
    }
    
    private static VerticesSortCriteria toSortCriteria(String individualCriteriaElementSpec) {
        String[] individualCriteriaParts = individualCriteriaElementSpec.trim().split(FIELDNAME_DIRECTION_DIVIDER);
        if(individualCriteriaParts.length != 2) {
            throw new IllegalArgumentException("Illegal sorting criteria format:'" + individualCriteriaElementSpec + "'");
        }
        VerticesSortCriteriaType sortType = VerticesSortCriteriaType.fromAlias(individualCriteriaParts[0]);
        SortDirection sortDirection = SortDirection.valueOf(individualCriteriaParts[1]);
        
        return new VerticesSortCriteria(sortType, sortDirection);
    }
    
    private static String serializeSortingCriteriaItem(@NonNull VerticesSortCriteria critariaItem) {
        return critariaItem.getSortingType().getAlias() + FIELDNAME_DIRECTION_DIVIDER + critariaItem.getSortDirection().name();
    }
}
