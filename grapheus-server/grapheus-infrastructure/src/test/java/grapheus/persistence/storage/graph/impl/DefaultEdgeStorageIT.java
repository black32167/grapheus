/**
 * 
 */
package grapheus.persistence.storage.graph.impl;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import grapheus.it.TestConstants;
import grapheus.it.util.GraphTestSupport;
import grapheus.persistence.exception.GraphExistsException;
import grapheus.persistence.storage.graph.EdgeStorage;

/**
 * @author black
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes={
        DefaultEdgeStorage.class
})
@TestPropertySource(TestConstants.DB_PROPERTIES)
public class DefaultEdgeStorageIT extends GraphTestSupport {
    private static final String GRAPH_NAME = "graph";
    @Inject
    private EdgeStorage edgeStorage;
    
    @Test
    public void testDisconnection() throws GraphExistsException {
        graph(GRAPH_NAME)
            .connect("v1", "v2")
            .connect("v2", "v3")
            .build();
        
        edgeStorage.disconnect(GRAPH_NAME, "v1", "v2");
        
        Assert.assertTrue(findInboundConnections(GRAPH_NAME, "v2").isEmpty());
        Assert.assertTrue(findOutboundConnections(GRAPH_NAME, "v1").isEmpty());
        Assert.assertEquals(1, findOutboundConnections(GRAPH_NAME, "v2").size());
    }
}
