/**
 * 
 */
package grapheus.persistence.storage.transaction.topology;

import grapheus.it.TestConstants;
import grapheus.it.util.GraphTestSupport;
import grapheus.persistence.exception.GraphExistsException;
import grapheus.persistence.storage.graph.impl.DefaultVertexStorage;
import grapheus.persistence.storage.graph.query.impl.DefaultVertexFinder;
import grapheus.persistence.storage.graph.transaction.topology.TopologicalMarkingTransaction;
import grapheus.persistence.testutil.DbTestsContextConfig;
import org.grapheus.client.model.graph.SortDirection;
import org.grapheus.client.model.graph.VerticesSortCriteria;
import org.grapheus.client.model.graph.VerticesSortCriteriaType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author black
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes={
        DbTestsContextConfig.class, DefaultVertexStorage.class, DefaultVertexFinder.class, TopologicalMarkingTransaction.class
})
@TestPropertySource(TestConstants.DB_PROPERTIES)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TopologicalMarkingTransactionIT extends GraphTestSupport {
    private final static String GRAPH_NAME = "graph1";
    
    @Inject
    private TopologicalMarkingTransaction topoTransaction;
    
    @Test
    public void testTopologicalSort1() throws GraphExistsException {
        graph(GRAPH_NAME).
            connect("v1", "v2").
            connect("v2", "v3").
            connect("v1", "v3").
            build();
        
        boolean cycleFound = topoTransaction.topologicalOrder(GRAPH_NAME);
        Assert.assertFalse(cycleFound);
        Collection<String> verticesKeys = findVerticesKeys(
                GRAPH_NAME,
                new VerticesSortCriteria(VerticesSortCriteriaType.TOPOLOGICAL, SortDirection.ASC));
        Assert.assertTrue(
                Arrays.asList("v3", "v2", "v1").equals(verticesKeys));
        
    }

    @Test
    public void testTopologicalSort2() throws GraphExistsException {
        graph(GRAPH_NAME).
            connect("v1", "v2").
            connect("v2", "v3").
            connect("v1", "v3").
            connect("v4", "v3").
            build();
        
        boolean cycleFound = topoTransaction.topologicalOrder(GRAPH_NAME);
        Assert.assertFalse(cycleFound);
        Collection<String> verticesKeys = findVerticesKeys(
                GRAPH_NAME,
                new VerticesSortCriteria(VerticesSortCriteriaType.TOPOLOGICAL, SortDirection.ASC));
        Assert.assertTrue(
                Arrays.asList("v3", "v4", "v2", "v1").equals(verticesKeys)
                || Arrays.asList("v3",  "v2", "v1", "v4").equals(verticesKeys));
    }

    @Test
    public void testTopologicalSortCycle() throws GraphExistsException {
        graph(GRAPH_NAME).
            connect("v1", "v2").
            connect("v2", "v3").
            connect("v3", "v1").
            build();
        
        boolean cycleFound = topoTransaction.topologicalOrder(GRAPH_NAME);
        
        Assert.assertTrue(cycleFound);
    }
}
