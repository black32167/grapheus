/**
 * 
 */
package org.grapheus.cli;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

import javax.inject.Inject;

import org.grapheus.cli.security.CLIUserContext;
import org.grapheus.client.GrapheusClientFactory;
import org.grapheus.client.UserClient;
import org.grapheus.client.http.auth.GrapheusClientCredentials;
import org.grapheus.client.model.graph.vertex.RVertex;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author black
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor=@__({@Inject}))
public class ArtifactsUploader {
    private static final Gson gson = new GsonBuilder().create();
    private final GrapheusClientFactory rcFactory;
    private final CLIUserContext userCtx;
        
    public void upload(String graph, Path targetPath) throws FileNotFoundException, IOException {

        Supplier<GrapheusClientCredentials> credsSupplier = userCtx.credentialsSupplier();
        
        UserClient userClient = rcFactory.forUser(credsSupplier);
        
        if(Files.isDirectory(targetPath)) {
            Files.walk(targetPath).filter(p -> p.getFileName().toString().endsWith(".json")).forEach(p->{
                System.out.println("Uploading file "+p);
                File file = p.toFile();
                uploadArtifactFromFile(graph, file, userClient);
                
            });
        } else {
            uploadArtifactFromFile(graph, targetPath.toFile(), userClient);
        }
    }

    private static void uploadArtifactFromFile(String graph, File file, UserClient userClient) {
        log.info("Uploading {}", file.getName());
        String artifactName = file.getName().replace(".json", "");
        
        try (FileReader freader = new FileReader(file)) {
          
            RVertex a = gson.fromJson(freader, RVertex.class);
          
            a.setTitle(artifactName);
            a.setDescription("");
            userClient.vertex().addVertex(graph, a);
        } catch (Exception e) {
            log.error("", e);
        } 
    }
}
