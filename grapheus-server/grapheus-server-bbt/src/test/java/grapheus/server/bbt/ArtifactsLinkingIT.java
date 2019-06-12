/**
 * 
 */
package grapheus.server.bbt;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.grapheus.client.api.VertexAPI;
import org.grapheus.client.model.graph.GraphNamesConstants;
import org.grapheus.client.model.graph.vertex.RVertex;
import org.grapheus.client.model.graph.vertex.RVerticesContainer;

import lombok.extern.slf4j.Slf4j;

/**
 * @author black
 *
 */
@Slf4j
@Ignore("As of 07-11-2018 we've put testing semantic linking functionality on hold")
public class ArtifactsLinkingIT extends AbstractUserBasedIT {
    private final static String GRAPH_NAME = GraphNamesConstants.DEFAULT_GRAPH_NAME;

    @Test
    public void crossRefsMatchingTest() {

        withUser((userKey, client) -> {
            VertexAPI vertexAPI = new VertexAPI(client);
            
            log.info("Creating user credentials");
            vertexAPI.addVertex(GRAPH_NAME, RVertex.builder().//
                    description("Mars base, blocked by MRS-1").//
                    title("Mars base").//
                    artifactId("MRS-2").//
                    build());
            
            try {
                Thread.sleep(500); 
            } catch  (Exception e) {}
            
            vertexAPI.addVertex(GRAPH_NAME, RVertex.builder().//
                    description("Spaceship, prerequisite for Mars program").//
                    title("Spaceship").//
                    artifactId("MRS-1").//
                    build());
            
            try {
                Thread.sleep(500); 
            } catch  (Exception e) {}
            
            
            vertexAPI.addVertex(GRAPH_NAME, RVertex.builder().//
                    description("Oxygen factory, blocked by MRS-2 and MRS-1").//
                    title("Spaceship").//
                    artifactId("MRS-3").//
                    build());
            
            try {
                Thread.sleep(500);
            } catch (Exception e) {}
            
            vertexAPI.addVertex(GRAPH_NAME, RVertex.builder().//
                    description("Oxygen factory, blocked by MRS-2").//
                    title("Spaceship").//
                    artifactId("MRS-3").//
                    build());
            
            try {
                Thread.sleep(500); 
            } catch (Exception e) {}
            
            vertexAPI.addVertex(GRAPH_NAME, RVertex.builder().//
                    description("Something unrelated desc").//
                    title("Something unrelated").//
                    artifactId("UNR-1").//
                    build());
            
            RVerticesContainer clustersEnvelope = client.get(
                    "artifact", RVerticesContainer.class, Collections.singletonMap("scope", getCurrentScope()));
            
            Assert.assertNotNull(clustersEnvelope);
            Assert.assertEquals(2, clustersEnvelope.getArtifacts().size());
                  
          
        });
    }

    @Test
    public void vectorSpaceMatchingTest() {

        withUser((userKey, client) -> {
            log.info("Creating user credentials");
            VertexAPI vertexAPI = new VertexAPI(client);
            vertexAPI.addVertex(GRAPH_NAME, RVertex.builder().//
                    description("Mars base").//
                    title("Mars base").//
                    artifactId("1").//
                    build());
            try {
                Thread.sleep(500);
            } catch  (Exception e) {}
            
            vertexAPI.addVertex(GRAPH_NAME, RVertex.builder().//
                    description("Spaceship, prerequisite for Mars base").//
                    title("Spaceship").//
                    artifactId("2").//
                    build());
            
            try {
                Thread.sleep(500); 
            } catch  (Exception e) {}
            
            
            vertexAPI.addVertex(GRAPH_NAME, RVertex.builder().//
                    description("Oxygen factory for Mars base").//
                    title("Spaceship").//
                    artifactId("3").
                    build());
            
            
            try {
                Thread.sleep(500);
            } catch (Exception e) {}
            vertexAPI.addVertex(GRAPH_NAME,  RVertex.builder().//
                    description("Something unrelated desc").//
                    title("Something unrelated").//
                    artifactId("UNR-1").//
                    build());
            
            RVerticesContainer clustersEnvelope = client.get(
                    "artifact", RVerticesContainer.class, Collections.singletonMap("scope", getCurrentScope()));
            
            Assert.assertNotNull(clustersEnvelope);
            Assert.assertEquals(2, clustersEnvelope.getArtifacts().size());
                  
          
        });
    }
    
}
