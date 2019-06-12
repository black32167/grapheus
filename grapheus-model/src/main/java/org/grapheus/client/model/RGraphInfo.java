/**
 * 
 */
package org.grapheus.client.model;

import java.util.List;

import org.grapheus.client.model.graph.VerticesSortCriteriaType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author black
 *
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RGraphInfo {
    private int verticesCount;
    private List<VerticesSortCriteriaType> availableSortCriteria;
}
