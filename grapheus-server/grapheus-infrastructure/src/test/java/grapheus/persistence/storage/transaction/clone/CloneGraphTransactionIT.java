/**
 * 
 */
package grapheus.persistence.storage.transaction.clone;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import grapheus.it.TestConstants;
import grapheus.it.util.GraphTestSupport;
import grapheus.persistence.exception.GraphExistsException;
import grapheus.persistence.storage.graph.transaction.clone.CloneGraphTransaction;

/**
 * @author black
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes={
        CloneGraphTransaction.class
})
@TestPropertySource(TestConstants.DB_PROPERTIES)
public class CloneGraphTransactionIT extends GraphTestSupport {

    private static final String GRAPH_NAME_SRC = "graph1";
    private static final String GRAPH_NAME_DST = "graph2";

    @Inject
    private CloneGraphTransaction transaction;
    
    @Test
    public void testClone() throws GraphExistsException {
        graph(GRAPH_NAME_SRC).connect("v1", "v2").build();
        graph(GRAPH_NAME_DST).build();
        
        transaction.generate(GRAPH_NAME_SRC, GRAPH_NAME_DST);
    }
}
