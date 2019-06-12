/**
 * 
 */
package grapheus.persistence.storage.transaction.merge;

import java.util.Arrays;
import java.util.Collection;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.grapheus.client.model.graph.SortDirection;
import org.grapheus.client.model.graph.VerticesSortCriteria;
import org.grapheus.client.model.graph.VerticesSortCriteriaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import grapheus.it.TestConstants;
import grapheus.it.util.GraphTestSupport;
import grapheus.persistence.exception.GraphExistsException;
import grapheus.persistence.model.graph.PersistentVertex;
import grapheus.persistence.storage.graph.query.impl.DefaultEdgesFinder;
import grapheus.persistence.storage.graph.transaction.merge.MergeVerticesTransaction;

/**
 * @author black
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes={
        MergeVerticesTransaction.class, DefaultEdgesFinder.class
})
@TestPropertySource(TestConstants.DB_PROPERTIES)
public class MergeVerticesTransactionIT extends GraphTestSupport {

    private static final String GRAPH_NAME = "graph";

    @Inject
    private MergeVerticesTransaction transaction;
    
    @Test
    @DirtiesContext
    public void testMerge() throws GraphExistsException {
        graph(GRAPH_NAME)
            .connect("v1", "v2")
            .connect("v2", "v4")
            .connect("v2", "v3")
            .build();
        

        updateVertex(GRAPH_NAME, PersistentVertex.builder().externalCompositeId("v2").description("descV2").build());
        updateVertex(GRAPH_NAME, PersistentVertex.builder().externalCompositeId("v3").description("descV3").build());

        
        String mergedId = transaction.merge("user", GRAPH_NAME, "Comp:new", Arrays.asList("v2", "v3"));
        
        Collection<String> inboundVertices = findInboundConnections(GRAPH_NAME, "v4");
        Assert.assertEquals(1, inboundVertices.size());
        
        Collection<String> outboundVertices = findOutboundConnections(GRAPH_NAME, "v1");
        Assert.assertEquals(1, outboundVertices.size());
        
        
        PersistentVertex merged = loadVertex(GRAPH_NAME, mergedId);
        Assert.assertTrue(merged.getDescription().contains("descV3"));
        Assert.assertTrue(merged.getDescription().contains("descV2"));
        Assert.assertTrue(merged.getUpdatedTimestamp() > 0);
        
        Collection<String> vertices = findVerticesKeys(GRAPH_NAME, new VerticesSortCriteria(VerticesSortCriteriaType.VERTEX_TITLE, SortDirection.ASC));
        Assert.assertEquals(3, vertices.size());
    }


    @Test
    @DirtiesContext
    public void testSiblingsMerge() throws GraphExistsException {
        graph(GRAPH_NAME)
            .connect("v1", "v2")
            .connect("v3", "v2")
            .build();
        
        transaction.merge("user", GRAPH_NAME, "Comp:new", Arrays.asList("v3","v1"));
        
        Collection<String> inboundVertices = findInboundConnections(GRAPH_NAME, "v2");
        Assert.assertEquals(1, inboundVertices.size());
        
        

        Collection<String> vertices = findVerticesKeys(GRAPH_NAME, new VerticesSortCriteria(VerticesSortCriteriaType.VERTEX_TITLE, SortDirection.ASC));
        Assert.assertEquals(2, vertices.size());
    }
}
