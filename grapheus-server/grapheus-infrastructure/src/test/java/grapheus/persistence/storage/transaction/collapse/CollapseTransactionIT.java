package grapheus.persistence.storage.transaction.collapse;

import grapheus.it.TestConstants;
import grapheus.it.util.GraphTestSupport;
import grapheus.persistence.exception.GraphExistsException;
import grapheus.persistence.model.graph.PersistentVertex;
import grapheus.persistence.storage.graph.transaction.collapse.CollapseTransaction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes={
        CollapseTransaction.class
})
@TestPropertySource(TestConstants.DB_PROPERTIES)
public class CollapseTransactionIT extends GraphTestSupport {
    private static final String GRAPH_NAME_SRC = "graph1";
    private static final String GRAPH_NAME_DST = "graph2";

    @Inject
    private CollapseTransaction transaction;

    @Test
    public void testCollapsedGraphShouldBeGenerated() throws GraphExistsException {
        String generativePropertyName = "prop1";
        graph(GRAPH_NAME_SRC)
                .connect("v1", "v2")
                .connect("v3", "v2")
                .prop("v1", generativePropertyName, "val1")
                .prop("v2", generativePropertyName, "val1")
                .build();
        graph(GRAPH_NAME_DST).build();

        transaction.generateCollapsedGraph(GRAPH_NAME_SRC, GRAPH_NAME_DST, generativePropertyName);

        PersistentVertex newVrtex = loadVertex(GRAPH_NAME_DST, "val1");
        assertEquals("val1", newVrtex.getGenerativeValue());
    }

}
