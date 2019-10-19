/**
 * 
 */
package org.grapheus.cli.subcommand.processor;

import lombok.RequiredArgsConstructor;
import org.grapheus.cli.security.CLIUserContext;
import org.grapheus.cli.subcommand.GrapheusCLICommand;
import org.grapheus.cli.subcommand.ListTasks;
import org.grapheus.client.GrapheusClientFactory;
import org.grapheus.client.api.VertexAPI;
import org.grapheus.client.api.VerticesFilter;
import org.grapheus.client.http.auth.GrapheusClientCredentials;
import org.grapheus.client.model.graph.vertex.RVerticesContainer;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * @author black
 */
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Service
public class ListTasksProcessor implements CommandProcessor {
    private final GrapheusClientFactory clientFactory;
    private final CLIUserContext userCtx;

    @Override
    public void process(GrapheusCLICommand command) throws CommandProcessingException {
        ListTasks taskCommand = (ListTasks) command;
        
        VertexAPI taskAPI = clientFactory.forUser(() -> new GrapheusClientCredentials(userCtx.getUserName(), userCtx.getPassword())).vertex();
        RVerticesContainer artifacts = taskAPI.findVertices(
                taskCommand.getGraph(),
                VerticesFilter.builder().build());
        
        System.out.println(artifacts);
    }

    @Override
    public Class<?> processingClass() {
        return ListTasks.class;
    }

}
