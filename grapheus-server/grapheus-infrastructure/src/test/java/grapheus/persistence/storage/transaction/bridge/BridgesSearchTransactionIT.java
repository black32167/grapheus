/**
 * 
 */
package grapheus.persistence.storage.transaction.bridge;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import grapheus.it.TestConstants;
import grapheus.it.util.GraphTestSupport;
import grapheus.persistence.exception.GraphExistsException;
import grapheus.persistence.storage.graph.impl.DefaultVertexStorage;
import grapheus.persistence.storage.graph.transaction.bridge.BridgesSearchTransaction;
import grapheus.persistence.storage.traverse.Edge;
import grapheus.persistence.testutil.DbTestsContextConfig;

/**
 * @author black
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes={
        DbTestsContextConfig.class, DefaultVertexStorage.class, BridgesSearchTransaction.class
})
@TestPropertySource(TestConstants.DB_PROPERTIES)
public class BridgesSearchTransactionIT extends GraphTestSupport {

    private final static String GRAPH_NAME = "graph1";
    
    private BridgesSearchTransaction transaction;

    @Test
    @DirtiesContext
    public void testNoCycleBridgeTraversal() throws GraphExistsException {
        graph(GRAPH_NAME).
            connect("v1", "v2").
            connect("v3", "v2").
            build();
   
        List<Edge> bridges = transaction.bridges(GRAPH_NAME);
        Assert.assertEquals(2, bridges.size());
    }
    

    @Test
    @DirtiesContext
    public void testSimpleCycleBridgeTraversal() throws GraphExistsException {
        graph(GRAPH_NAME).
            connect("v1", "v2").
            connect("v2", "v3").
            connect("v2", "v4").
            connect("v4", "v3").
            build();
        
        List<Edge> bridges = transaction.bridges(GRAPH_NAME);
        Assert.assertEquals(1, bridges.size());
        Edge bridge = bridges.get(0);
        Assert.assertTrue(bridge.contains("v1"));
        Assert.assertTrue(bridge.contains("v2"));
    }
    
    @Test
    @DirtiesContext
    public void testInterComponentsBridgeTraversal() throws GraphExistsException {
        graph(GRAPH_NAME).
            connect("v1", "v2").
            connect("v2", "v3").
            connect("v3", "v1").
            connect("v4", "v5").
            connect("v5", "v6").
            connect("v6", "v4").
            connect("v3", "v4").
            build();
            
        List<Edge> bridges = transaction.bridges(GRAPH_NAME);
        Assert.assertEquals(1, bridges.size());
        
        Edge bridge = bridges.get(0);
        Assert.assertTrue(bridge.contains("v4"));
        Assert.assertTrue(bridge.contains("v3"));
    }
    
}
