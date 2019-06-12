/**
 * 
 */
package grapheus.rest.converter;

import org.grapheus.client.model.graph.edge.REdge;
import org.junit.Assert;
import org.junit.Test;

import grapheus.persistence.model.graph.PersistentEdge;

/**
 * @author black
 *
 */
public class EdgeConverterTest {
    
    @Test
    public void convertingToExternalModelShouldStripGraphId() {
        REdge externalEdge = EdgeConverter.toExternalEdge(PersistentEdge.builder().from("graph/fromId").to("graph/toId").build());
        
        Assert.assertEquals("External model edge destination should not contain collection name", "toId", externalEdge.getTo());
        Assert.assertEquals("External model edge source should not contain collection name", "fromId", externalEdge.getFrom());
    }

    @Test
    public void convertingToInternalIdsShouldBePrefixedWithVertexCollectionName() {
        PersistentEdge internalEdge = EdgeConverter.toInternal("vertexCollection", REdge.builder().from("fromId").to("toId").build());
        
        Assert.assertEquals("Internal model edge destination should contain graphId", "vertexCollection/toId", internalEdge.getTo());
        Assert.assertEquals("Internal model edge source should contain graphId", "vertexCollection/fromId", internalEdge.getFrom());
        
    }
}
