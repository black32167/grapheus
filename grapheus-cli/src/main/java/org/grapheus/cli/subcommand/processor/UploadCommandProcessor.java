/**
 * 
 */
package org.grapheus.cli.subcommand.processor;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Inject;

import org.grapheus.cli.ArtifactsUploader;
import org.grapheus.cli.subcommand.GrapheusCLICommand;
import org.grapheus.cli.subcommand.UploadCommand;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * @author black
 *
 */
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Service
public class UploadCommandProcessor implements CommandProcessor {
    private final ArtifactsUploader artifactsUploader;

    @Override
    public void process(GrapheusCLICommand command) throws CommandProcessingException {
        UploadCommand uploadCommand = (UploadCommand) command;
        if(uploadCommand.getPath() == null) {
            throw new IllegalArgumentException("Upload path is not specified");
        }
        Path absPath = Paths.get(uploadCommand.getPath()).toAbsolutePath();
        try {
            artifactsUploader.upload(uploadCommand.getGraph(), absPath);
        } catch (IOException e) {
            throw new CommandProcessingException("Cannot upload data from " + absPath.toString(), e);
        }
    }

    @Override
    public Class<?> processingClass() {
        return UploadCommand.class;
    }
}
