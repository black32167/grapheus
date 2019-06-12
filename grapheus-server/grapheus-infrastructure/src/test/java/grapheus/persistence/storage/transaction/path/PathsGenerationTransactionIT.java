/**
 * 
 */
package grapheus.persistence.storage.transaction.path;

import static java.util.Arrays.asList;
import javax.inject.Inject;

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
import grapheus.persistence.storage.graph.transaction.paths.PathsGenerationTransaction;

/**
 * @author black
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes={
        PathsGenerationTransaction.class
})
@TestPropertySource(TestConstants.DB_PROPERTIES)
public class PathsGenerationTransactionIT extends GraphTestSupport {

    private static final String GRAPH_NAME = "graph";
    private static final String NEW_GRAPH_NAME = "new_graph";

    @Inject
    private PathsGenerationTransaction transaction;
    

    @Test
    @DirtiesContext
    public void testTermnalCycle() throws GraphExistsException {
        graph(GRAPH_NAME)
            .connect("v1", "v2")
            .connect("v2", "v3")
            .connect("v3", "v4")
            .connect("v4", "v2")
            .build();
        graph(NEW_GRAPH_NAME)
            .build();
        
        transaction.findPaths(GRAPH_NAME, NEW_GRAPH_NAME, asList("v1"));
        
        Assert.assertEquals(1, findOutboundConnections(NEW_GRAPH_NAME, "v1").size());
       
        
    }
    
    @Test
    @DirtiesContext
    public void testFindSimplPath() throws GraphExistsException {
        graph(GRAPH_NAME)
            .connect("v1", "v2")
            .connect("v2", "v4")
            .connect("v2", "v3")
            .build();
        graph(NEW_GRAPH_NAME)
            .build();
        
        transaction.findPaths(GRAPH_NAME, NEW_GRAPH_NAME, asList("v1", "v3"));
        
        Assert.assertEquals(1, findInboundConnections(NEW_GRAPH_NAME, "v3").size());
        Assert.assertEquals(1, findInboundConnections(NEW_GRAPH_NAME, "v2").size());
        
    }
    

    @Test
    @DirtiesContext
    public void testFindSimplPathTwice() throws GraphExistsException {
        graph(GRAPH_NAME)
            .connect("v1", "v2")
            .connect("v2", "v4")
            .connect("v2", "v3")
            .build();
        graph(NEW_GRAPH_NAME)
            .build();
        
        transaction.findPaths(GRAPH_NAME, NEW_GRAPH_NAME, asList("v2", "v3"));
        transaction.findPaths(GRAPH_NAME, NEW_GRAPH_NAME, asList("v1", "v3"));
        
        Assert.assertEquals(1, findInboundConnections(NEW_GRAPH_NAME, "v3").size());
        Assert.assertEquals(1, findInboundConnections(NEW_GRAPH_NAME, "v2").size());
        
    }
    
    @Test
    @DirtiesContext
    public void testFindDiamondPath() throws GraphExistsException {
        graph(GRAPH_NAME)
            .connect("v1", "v2")
            .connect("v2", "v4")
            .connect("v2", "v3")
            .connect("v2", "v5")
            .connect("v4", "v6")
            .connect("v3", "v6")
            .build();
        graph(NEW_GRAPH_NAME)
            .build();
        
        transaction.findPaths(GRAPH_NAME, NEW_GRAPH_NAME, asList("v1", "v6"));
        
        Assert.assertEquals(1, findOutboundConnections(NEW_GRAPH_NAME, "v1").size());
        Assert.assertEquals(2, findInboundConnections(NEW_GRAPH_NAME, "v6").size());
        Assert.assertEquals(2, findOutboundConnections(NEW_GRAPH_NAME, "v2").size());
        Assert.assertEquals(1, findInboundConnections(NEW_GRAPH_NAME, "v2").size());
    }

}
