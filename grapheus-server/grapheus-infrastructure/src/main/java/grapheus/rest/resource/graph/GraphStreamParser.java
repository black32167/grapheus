/**
 * 
 */
package grapheus.rest.resource.graph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.grapheus.client.model.GsonFactory;
import org.grapheus.client.model.graph.GraphStreamFields;
import org.grapheus.client.model.graph.edge.REdge;
import org.grapheus.client.model.graph.vertex.RVertex;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import lombok.Builder;

/**
 * Transforms zipped graph stream into vertices and edges.
 * 
 * @author black
 */
@Builder
public final class GraphStreamParser {
    private static final Gson GSON = GsonFactory.createGson();
    
    private Consumer<RVertex> vertexConsumer;
    private Consumer<REdge> edgeConsumer;
    
    /**
     * Decodes zip stream and invokes visitor with decoded vertices and edges.
     * @throws IOException 
     */
    public void consumeStream(InputStream zippedGraphStream) throws IOException {
        try(
                ZipInputStream zis = new ZipInputStream(zippedGraphStream);
                BufferedReader br = new BufferedReader(new InputStreamReader(zis))) {
            
            ZipEntry entry;
            JsonReader jReader = GSON.newJsonReader(br);
            
            while((entry = zis.getNextEntry()) != null) {
  
                jReader.beginObject();
                
                while(jReader.hasNext()) {
                    String fieldName = jReader.nextName();
                    switch (fieldName) {
                    case GraphStreamFields.FIELD_VERTICES: importVertices(jReader); break;
                    case GraphStreamFields.FIELD_EDGES: importEdges(jReader); break;
                    }
                }
                jReader.endObject();
            }
        }
    }

    private void importVertices(JsonReader jReader) throws IOException {
        jReader.beginArray();
        while(jReader.hasNext()) {
            RVertex vertex = GSON.fromJson(jReader, RVertex.class);
            if(vertexConsumer != null) {
                vertexConsumer.accept(vertex);
            }
        }
        jReader.endArray();
    }
    
    private void importEdges(JsonReader jReader) throws IOException {
        jReader.beginArray();
        while(jReader.hasNext()) {
            REdge edge = GSON.fromJson(jReader, REdge.class);
            if(edgeConsumer != null) {
                edgeConsumer.accept(edge);
            }
        }
        jReader.endArray();
    }

}
