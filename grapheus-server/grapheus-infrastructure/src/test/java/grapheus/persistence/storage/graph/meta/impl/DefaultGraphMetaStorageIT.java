/**
 * 
 */
package grapheus.persistence.storage.graph.meta.impl;

import java.util.Collection;
import java.util.Collections;

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
import grapheus.persistence.StorageSupport;
import grapheus.persistence.exception.GraphExistsException;
import grapheus.persistence.model.graph.Graph;
import grapheus.persistence.storage.graph.GraphStorage;
import grapheus.persistence.storage.graph.impl.DefaultGraphStorage;
import grapheus.persistence.testutil.DbTestsContextConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes={
        DbTestsContextConfig.class, DefaultGraphStorage.class
})
@TestPropertySource(TestConstants.DB_PROPERTIES)
public class DefaultGraphMetaStorageIT extends StorageSupport {
    private final static String USER_KEY1 = "userKey1";
    private final static String USER_KEY2 = "userKey2";
    private final static String GRAPH_QUALIFIER_1 = "graph1";
    private final static String GRAPH_QUALIFIER_2 = "graph2";
    private final static String GRAPH_QUALIFIER_3 = "graph3";
    
    @Inject
    private GraphStorage storage;
    
    @Test
    @DirtiesContext
    public void testHappy() throws GraphExistsException {
        Graph g1 = storage.addGraph(GRAPH_QUALIFIER_1);
        g1.setUserKeys(Collections.singletonList(USER_KEY1));
        storage.updateGraphMeta(g1);
        
        Graph g2 = storage.addGraph(GRAPH_QUALIFIER_2);
        g2.setUserKeys(Collections.singletonList(USER_KEY1));
        storage.updateGraphMeta(g2);
        
        Graph g3 = storage.addGraph(GRAPH_QUALIFIER_3);
        g3.setUserKeys(Collections.singletonList(USER_KEY2));
        storage.updateGraphMeta(g3);
        
        Collection<Graph> graphNamesForUser1 = storage.getUserGraphs(USER_KEY1);
        Assert.assertEquals(2,  graphNamesForUser1.size());
        
        Collection<Graph> graphNamesForUser2 = storage.getUserGraphs(USER_KEY2);
        Assert.assertEquals(1,  graphNamesForUser2.size());
        
    }
    
    @Test(expected=GraphExistsException.class)
    @DirtiesContext
    public void testDuplicateGraph() throws GraphExistsException {
        storage.addGraph(GRAPH_QUALIFIER_1);
        storage.addGraph(GRAPH_QUALIFIER_1);
    }
    

    @Test(expected=GraphExistsException.class)
    @DirtiesContext
    public void testConflict() throws GraphExistsException {

        Graph g1 = storage.addGraph(GRAPH_QUALIFIER_1);
        String initialRev = g1.getRev();
        g1.setUserKeys(Collections.singletonList(USER_KEY1));
        storage.updateGraphMeta(g1);
        
        g1.setUserKeys(Collections.singletonList(USER_KEY2));
        g1.setRev(initialRev);
        storage.updateGraphMeta(g1);
    }

}
