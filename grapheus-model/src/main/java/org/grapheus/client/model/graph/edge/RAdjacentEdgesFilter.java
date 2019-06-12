/**
 * 
 */
package org.grapheus.client.model.graph.edge;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author black
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RAdjacentEdgesFilter {
    private final static String PARTS_DIVIDER = "_";
    
    private int amount = 0;
    private EdgeDirection direction = EdgeDirection.OUTBOUND;
    
    public static RAdjacentEdgesFilter deserialize(String serialized) {
        if(serialized == null) {
            return null;
        }
        String[] parts = serialized.split(PARTS_DIVIDER);
        if(parts.length != 2) throw new IllegalArgumentException("Invalid specification of 'adjacent edges filter': '" + serialized + "'");
        
        int parsedAmount = Integer.parseInt(parts[0]);
        EdgeDirection parsedDirection = EdgeDirection.valueOf(parts[1]);
        
        return new RAdjacentEdgesFilter(parsedAmount, parsedDirection);
    }

    public String serialize() {
        return amount + PARTS_DIVIDER + direction.name();
    }
}
