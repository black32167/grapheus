/**
 * 
 */
package grapheus.rest.resource.graph;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.grapheus.client.model.graph.GraphStreamFields;
import org.grapheus.client.model.graph.edge.REdge;
import org.grapheus.client.model.graph.vertex.RVertex;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

import lombok.Builder;
import lombok.NonNull;

/**
 * Serializes graph to zipped stream
 * 
 * @author black
 */
@Builder
public class GraphStreamSerializer {
    private static final Gson GSON = GsonFactory.createGson();
    
    @NonNull private final Iterable<RVertex> verticesProducer;
    @NonNull private final Iterable<REdge> edgesProducer;
    private final String graphId;
    
    public void serialize(OutputStream output) throws IOException {

        ZipOutputStream zippedOS = new ZipOutputStream(output);
        zippedOS.putNextEntry(new ZipEntry(graphId + ".json"));
        
        
        Writer bufWriter = new BufferedWriter(new OutputStreamWriter(zippedOS));
        JsonWriter writer = GSON.newJsonWriter(bufWriter);
        writer.beginObject();
        
        writeVertices(writer, graphId);
        writeEdges(writer, graphId);
        
        writer.endObject();
        
        bufWriter.flush();//flush();
        
        zippedOS.closeEntry();
        zippedOS.finish();
    }
    
    private void writeVertices(JsonWriter writer, String graphId) throws IOException {
        writer.name(GraphStreamFields.FIELD_VERTICES);
        writer.beginArray();
       
        verticesProducer//
                .forEach(vertex -> GSON.toJson(vertex, RVertex.class, writer));

        writer.endArray();
    }
    
    private void writeEdges(JsonWriter writer, String graphId) throws IOException {
        writer.name(GraphStreamFields.FIELD_EDGES);
        writer.beginArray();
       
        edgesProducer//
                .forEach(edge -> GSON.toJson(edge, REdge.class, writer));

        writer.endArray();
    }
    
}
