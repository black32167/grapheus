/**
 * 
 */
package org.grapheus.client.model.graph.edge;

import org.junit.Assert;
import org.junit.Test;


/**
 * @author black
 */
public class RAdjacentEdgesFilterTest {
    private final static String SER_1_OUT = "1_OUTBOUND";
    
    @Test
    public void testDeserialization() {
        RAdjacentEdgesFilter f = RAdjacentEdgesFilter.deserialize(SER_1_OUT);
        Assert.assertEquals(1, f.getAmount());
        Assert.assertEquals(EdgeDirection.OUTBOUND, f.getDirection());
    }
    

    @Test
    public void testSerialization() {
        String serialized = new RAdjacentEdgesFilter(1, EdgeDirection.OUTBOUND).serialize();
        Assert.assertEquals(serialized, SER_1_OUT);
    }

}
