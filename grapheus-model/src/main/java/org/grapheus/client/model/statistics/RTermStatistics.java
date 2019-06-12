package org.grapheus.client.model.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Container for statistics of terms
 * known by system.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RTermStatistics {
    private List<RTermData> termCounts;
    private int totalCount;
}
