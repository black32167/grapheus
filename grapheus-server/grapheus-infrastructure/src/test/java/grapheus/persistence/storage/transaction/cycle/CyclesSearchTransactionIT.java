/**
 * 
 */
package grapheus.persistence.storage.transaction.cycle;

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
import grapheus.persistence.storage.graph.transaction.cycle.CyclesSearchTransaction;
import grapheus.persistence.testutil.DbTestsContextConfig;

/**
 * @author black
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes={
        DbTestsContextConfig.class, DefaultVertexStorage.class, CyclesSearchTransaction.class
})
@TestPropertySource(TestConstants.DB_PROPERTIES)
public class CyclesSearchTransactionIT extends GraphTestSupport {
    private final static String GRAPH_NAME = "graph1";
    
    @Inject
    private CyclesSearchTransaction transaction;
    
    @Test
    @DirtiesContext
    public void testCycles() throws GraphExistsException {
        graph(GRAPH_NAME).
            connect("v1", "v2").
            connect("v2", "v3").
            connect("v3", "v4").
            connect("v4", "v2").
            connect("v4", "v3").
            connect( "v5", "v3").
            build();
        
        List<List<String>> cycles = transaction.cycles(GRAPH_NAME);
        Assert.assertNotNull(cycles);
        Assert.assertEquals(2, cycles.size());
    }
}
