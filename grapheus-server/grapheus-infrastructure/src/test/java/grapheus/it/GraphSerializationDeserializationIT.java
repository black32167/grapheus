/**
 * 
 */
package grapheus.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.grapheus.client.model.graph.edge.REdge;
import org.grapheus.client.model.graph.vertex.RVertex;
import org.junit.Test;

import grapheus.rest.resource.graph.GraphStreamParser;
import grapheus.rest.resource.graph.GraphStreamSerializer;

/**
 * @author black
 *
 */
public class GraphSerializationDeserializationIT {

    @Test
    public void deserializerShouldReadSerializedContent() throws IOException {
        // Serializing
        Collection<REdge> edges = Arrays.asList(REdge.builder().from("fromId").to("toId").build());
        Collection<RVertex> vertices = Arrays.asList(
                RVertex.builder().localId("fromId").build(),
                RVertex.builder().localId("toId").build());
        GraphStreamSerializer serializer = GraphStreamSerializer.builder()
                .edgesProducer(edges)
                .verticesProducer(vertices)
                .graphId("graphId")
                .build();
        
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        
        serializer.serialize(os);
        
        // Deserializing
        final Collection<REdge> deserializedEdges = new ArrayList<REdge>();
        final Collection<RVertex> deserializedVertices = new ArrayList<RVertex>();
        GraphStreamParser deserializer = GraphStreamParser.builder()
                .edgeConsumer(deserializedEdges::add)
                .vertexConsumer(deserializedVertices::add) 
                .build();
        deserializer.consumeStream(new ByteArrayInputStream(os.toByteArray()));
        
        // Check
        assertEquals("Deserialized edges collection should contain 1 element", 1, deserializedEdges.size());
        assertTrue("Deserialized edges collection should contain 'valid directed edge", deserializedEdges.stream().anyMatch(e->e.getFrom().equals("fromId") && e.getTo().equals("toId")));
        
        assertEquals("Deserialized vertices collection should contain 2 elements", 2, deserializedVertices.size());
        assertTrue("Deserialized vertices collection should contain 'from' vertex", deserializedVertices.stream().anyMatch(v->v.getLocalId().equals("fromId")));
        assertTrue("Deserialized vertices collection should contain 'to' vertex", deserializedVertices.stream().anyMatch(v->v.getLocalId().equals("toId")));
    }
}
