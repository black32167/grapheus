/**
 * 
 */
package grapheus.persistence.storage.artifact;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import grapheus.it.TestConstants;
import grapheus.persistence.exception.DocumentsConflictException;
import grapheus.persistence.exception.GraphExistsException;
import grapheus.persistence.model.graph.PersistentVertex;
import grapheus.persistence.storage.graph.GraphStorage;
import grapheus.persistence.storage.graph.VertexStorage;
import grapheus.persistence.storage.graph.impl.DefaultVertexStorage;
import grapheus.persistence.testutil.DbTestsContextConfig;
import grapheus.view.SemanticFeature;

/**
 * @author black
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes={
        DbTestsContextConfig.class, DefaultVertexStorage.class
})
@TestPropertySource(TestConstants.DB_PROPERTIES)
public class DefaultGraphsStorageIT {
    private static final String GRAPH_NAME = "graph1";

    @Inject
    private GraphStorage graphStorage;

    @Inject
    private VertexStorage vertexStorage;
    
    @Test(expected=DocumentsConflictException.class)
    public void testUpdateVertexConflict() throws GraphExistsException {
        graphStorage.addGraph(GRAPH_NAME);
        
        PersistentVertex v = PersistentVertex.builder().externalCompositeId("id").build();
        vertexStorage.createVertex(GRAPH_NAME, v);
        String initialRev = v.getRev();
        vertexStorage.updateVertex(GRAPH_NAME, v);
        v.setRev(initialRev);
        vertexStorage.updateVertex(GRAPH_NAME, v);
    }

    @Test
    public void testGetAllArtifactsProperties() throws GraphExistsException {
        final String f1Name = "f1";
        final String f2Name = "f2";
        final String f3Name = "f3";
        graphStorage.addGraph(GRAPH_NAME);
        
        List<SemanticFeature> semanticFeatures1 = new ArrayList<SemanticFeature>();
        semanticFeatures1.add(SemanticFeature.from(f1Name, "v1"));
        semanticFeatures1.add(SemanticFeature.from(f2Name, "v2"));
        PersistentVertex v1 = PersistentVertex.builder().//
                externalCompositeId("id1").//
                semanticFeatures(semanticFeatures1).//
                build();
        vertexStorage.createVertex(GRAPH_NAME, v1);
        
        List<SemanticFeature> semanticFeatures2 = new ArrayList<SemanticFeature>();
        semanticFeatures2.add(SemanticFeature.from(f1Name, "v3"));
        semanticFeatures2.add(SemanticFeature.from(f3Name, "v2"));
        PersistentVertex v2 = PersistentVertex.builder().//
                externalCompositeId("id2").//
                semanticFeatures(semanticFeatures2).//
                build();
        vertexStorage.createVertex(GRAPH_NAME, v2);
        
        List<String> properties = vertexStorage.getAllArtifactsProperties(GRAPH_NAME);
        Assert.assertEquals(3, properties.size());
        Assert.assertTrue(properties.contains(f1Name));
        Assert.assertTrue(properties.contains(f2Name));
        Assert.assertTrue(properties.contains(f3Name));
    }
   
}
