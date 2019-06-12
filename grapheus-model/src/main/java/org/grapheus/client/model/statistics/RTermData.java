package org.grapheus.client.model.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Individual term's statistics
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RTermData {
    private String term;
    private int count;
}
