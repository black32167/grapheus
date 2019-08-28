/**
 * 
 */
package grapheus.persistence.storage.transaction.traverse;

import grapheus.it.TestConstants;
import grapheus.it.util.GraphTestSupport;
import grapheus.persistence.exception.GraphExistsException;
import grapheus.persistence.storage.graph.transaction.traverse.CloneSubgraphTransaction;
import org.grapheus.client.model.graph.SortDirection;
import org.grapheus.client.model.graph.VerticesSortCriteria;
import org.grapheus.client.model.graph.VerticesSortCriteriaType;
import org.grapheus.client.model.graph.edge.EdgeDirection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.inject.Inject;
import java.util.Collection;

/**
 * @author black
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes={
        CloneSubgraphTransaction.class
})
@TestPropertySource(TestConstants.DB_PROPERTIES)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CloneSubgraphTransactionIT extends GraphTestSupport {
    private static final String GRAPH_NAME_SRC = "graph1";
    private static final String GRAPH_NAME_DST = "graph2";

    @Inject
    private CloneSubgraphTransaction transaction;
    
    @Test
    public void testTraversal2vGraph1Outbound() throws GraphExistsException {
        graph(GRAPH_NAME_SRC).connect("v1", "v2").build();
        graph(GRAPH_NAME_DST).build();
        transaction.generate(GRAPH_NAME_SRC, GRAPH_NAME_DST, "v1", EdgeDirection.OUTBOUND);
        Collection<String> verticesKeys = findVerticesKeys(
                GRAPH_NAME_DST,
                new VerticesSortCriteria(VerticesSortCriteriaType.TOPOLOGICAL, SortDirection.ASC));
        Assert.assertEquals(2,  verticesKeys.size());
    }
    
    @Test
    public void testTraversal2vGraph1Inbound() throws GraphExistsException {
        graph(GRAPH_NAME_SRC).connect("v1", "v2").build();
        graph(GRAPH_NAME_DST).build();
        transaction.generate(GRAPH_NAME_SRC, GRAPH_NAME_DST, "v1", EdgeDirection.INBOUND);
        Collection<String> verticesKeys = findVerticesKeys(
                GRAPH_NAME_DST,
                new VerticesSortCriteria(VerticesSortCriteriaType.TOPOLOGICAL, SortDirection.ASC));
        Assert.assertEquals(1,  verticesKeys.size());
    }

    @Test
    public void testTraversalGraph3CycleOutbound() throws GraphExistsException {
        graph(GRAPH_NAME_SRC)
            .connect("v1", "v2")
            .connect("v2", "v3")
            .connect("v3", "v1")
            .build();
        graph(GRAPH_NAME_DST).build();
        transaction.generate(GRAPH_NAME_SRC, GRAPH_NAME_DST, "v1", EdgeDirection.OUTBOUND);
        Collection<String> verticesKeys = findVerticesKeys(
                GRAPH_NAME_DST,
                new VerticesSortCriteria(VerticesSortCriteriaType.TOPOLOGICAL, SortDirection.ASC));
        Assert.assertEquals(3,  verticesKeys.size());
    }

    @Test
    public void testTraversalGraph3CycleInbound() throws GraphExistsException {
        graph(GRAPH_NAME_SRC)
            .connect("v1", "v2")
            .connect("v2", "v3")
            .connect("v3", "v1")
            .build();
        graph(GRAPH_NAME_DST).build();
        transaction.generate(GRAPH_NAME_SRC, GRAPH_NAME_DST, "v1", EdgeDirection.INBOUND);
        Collection<String> verticesKeys = findVerticesKeys(
                GRAPH_NAME_DST,
                new VerticesSortCriteria(VerticesSortCriteriaType.TOPOLOGICAL, SortDirection.ASC));
        Assert.assertEquals(3,  verticesKeys.size());
    }
}
