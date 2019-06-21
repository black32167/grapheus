/**
 * 
 */
package org.grapheus.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.grapheus.client.GrapheusClientFactory;
import org.grapheus.client.UserClient;
import org.grapheus.client.http.auth.GrapheusClientCredentials;
import org.grapheus.client.model.graph.GraphNamesConstants;
import org.grapheus.client.model.graph.vertex.RVertex;


/**
 * @author black
 *
 */
//@Slf4j
public class ClassUploader {
    private static final String BASE_URL = "http://127.0.0.1:8081/grapheus";
    private static final String USER = System.getenv("uname");
    private static final String PASSWORD = System.getenv("upassword");

    public static void main(String[] args) throws FileNotFoundException, IOException {
        GrapheusClientFactory rcFactory = new GrapheusClientFactory(BASE_URL);
        Supplier<GrapheusClientCredentials> credsSupplier = () -> new GrapheusClientCredentials(USER, PASSWORD.getBytes(Charset.forName("UTF-8")));
        
        UserClient userClient = rcFactory.forUser(credsSupplier);
        
        Path targetFolder = Paths.get(args[0]).toAbsolutePath();
        Files.walk(targetFolder).filter(p -> p.getFileName().toString().endsWith(".java")).forEach(p->{
            System.out.println("Uploading file "+p);
            File file = p.toFile();
            String className = file.getName().replace(".java", "");
            try {
                String fileContent = IOUtils.readLines(new FileInputStream(file), Charset.forName("UTF-8")).stream().//
                        filter(l -> !l.trim().startsWith("package")).//
                        filter(l -> !l.trim().startsWith("import")).//
                        collect(Collectors.joining("\n"));

                userClient.vertex().addVertex(GraphNamesConstants.DEFAULT_GRAPH_NAME, RVertex.builder().//
                        description(fileContent).//
                        title(className).//
                        id(className).//
                        build());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
