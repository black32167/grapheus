/**
 * 
 */
package grapheus.persistence.storage.traverse;

import static java.util.Collections.singletonList;

import java.util.List;

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
import grapheus.persistence.storage.graph.impl.DefaultVertexStorage;
import grapheus.persistence.storage.graph.query.EdgesFinder;
import grapheus.persistence.storage.graph.query.impl.DefaultEdgesFinder;
import grapheus.persistence.testutil.DbTestsContextConfig;

/**
 * @author black
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes={
        DbTestsContextConfig.class, DefaultVertexStorage.class,  DefaultEdgesFinder.class, 
})
@TestPropertySource(TestConstants.DB_PROPERTIES)
public class EdgesFinderIT extends GraphTestSupport {
    private final static String GRAPH_NAME = "graph1";
    @Inject
    private EdgesFinder edgesFinder;
    
    @Test
    @DirtiesContext
    public void testGettingAllTransitiveDependencies() throws GraphExistsException {
        graph(GRAPH_NAME).
            connect("v1", "v2").
            connect("v2", "v3").
            connect("v2", "v4").
            connect("v4", "v3").
            build();
        
        List<Edge> outbounds1 = edgesFinder.outbound(GRAPH_NAME, singletonList("v2"), 1);
        Assert.assertEquals(2, outbounds1.size());
        Assert.assertTrue(outbounds1.contains(Edge.builder().from("v2").to("v4").build()));
        Assert.assertTrue(outbounds1.contains(Edge.builder().from("v2").to("v3").build()));
        
        List<Edge> outbounds2 = edgesFinder.outbound(GRAPH_NAME, singletonList("v1"), 1);
        Assert.assertEquals(1, outbounds2.size());
        Assert.assertTrue(outbounds2.contains(Edge.builder().from("v1").to("v2").build()));

        
        List<Edge> outbounds3 = edgesFinder.outbound(GRAPH_NAME, singletonList("v1"), 2);
        Assert.assertEquals(3, outbounds3.size());
        Assert.assertTrue(outbounds2.contains(Edge.builder().from("v1").to("v2").build()));
        Assert.assertTrue(outbounds3.contains(Edge.builder().from("v2").to("v3").build()));
        Assert.assertTrue(outbounds3.contains(Edge.builder().from("v2").to("v4").build()));
       
    }
    
    
//    @Test
//    public void test() {
//        List<Bridge> bridges = graphTraversal.bridges();
//        Assert.assertEquals(381, bridges.size());
//        
//    }
    

}
