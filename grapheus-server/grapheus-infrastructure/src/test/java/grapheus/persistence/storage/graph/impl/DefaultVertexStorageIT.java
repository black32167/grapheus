/**
 * 
 */
package grapheus.persistence.storage.graph.impl;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.google.common.collect.Lists;

import grapheus.it.TestConstants;
import grapheus.it.util.GraphTestSupport;
import grapheus.persistence.exception.GraphExistsException;
import grapheus.persistence.model.graph.PersistentVertex;

/**
 * @author black
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes={
        DefaultVertexStorage.class
})
@TestPropertySource(TestConstants.DB_PROPERTIES)
public class DefaultVertexStorageIT extends GraphTestSupport {

    private static final String GRAPH_NAME = "graph";
    @Inject
    private DefaultVertexStorage storage;
    
    @Test
    public void getAllVertices() throws GraphExistsException {
        PersistentVertex.PersistentVertexBuilder vertexBlueprint = PersistentVertex.builder()
                .description("description");

        graph(GRAPH_NAME)
            .vertex(vertexBlueprint.title("v1").build())
            .vertex(vertexBlueprint.title("v2").build())
            .vertex(vertexBlueprint.title("v3").build())
            .build();
        
        List<PersistentVertex> vertices = Lists.newArrayList(storage.getAllVertices(GRAPH_NAME));
        assertEquals(3,  vertices.size());
    }
    
    @Test
    public void testPartialUpdate() throws GraphExistsException {
        PersistentVertex vertex = PersistentVertex.builder()
                .title("title")
                .description("description")
                .id("id")
                .build();
        
        graph(GRAPH_NAME).vertex(vertex).build();
        
        PersistentVertex partialVertex = new PersistentVertex();
        partialVertex.setTitle("title1");
        partialVertex.setId("id");
        storage.partiallyUpdateVertex(GRAPH_NAME, partialVertex);
        
        PersistentVertex updatedVertex = loadVertex(GRAPH_NAME, "id");
        
        assertEquals("description", updatedVertex.getDescription());
        assertEquals("title1", updatedVertex.getTitle());
    }
    
    @Test
    public void testVertexDelete() throws GraphExistsException {
        PersistentVertex vertex = PersistentVertex.builder()
                .title("title")
                .description("description")
                .id("id")
                .build();
        
        graph(GRAPH_NAME).vertex(vertex).build();
        storage.deleteVertex(GRAPH_NAME, "id");
    }
    
}
