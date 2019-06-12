/**
 * 
 */
package org.grapheus.cli.subcommand.processor;

import javax.inject.Inject;

import org.grapheus.cli.remote.ServerAccountManager;
import org.grapheus.cli.security.CLIUserContext;
import org.grapheus.cli.subcommand.GrapheusCLICommand;
import org.grapheus.cli.subcommand.delete.DeleteCommand;
import org.grapheus.cli.subcommand.delete.DeleteGraphCommand;
import org.grapheus.cli.subcommand.delete.DeleteUserCommand;
import org.grapheus.client.GrapheusClientFactory;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * @author black
 *
 */
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Service
public class DeleteProfileCommandProcessor implements CommandProcessor {

    private final ServerAccountManager serverAccountManager;
    private final GrapheusClientFactory rcFactory;
    private final CLIUserContext userCtx;


    @Override
    public void process(GrapheusCLICommand command) throws CommandProcessingException {
        DeleteCommand deleteCommand = (DeleteCommand) command;
        GrapheusCLICommand subCommand = deleteCommand.getCommand();
        if(subCommand instanceof DeleteGraphCommand) {
            rcFactory.forUser(userCtx.credentialsSupplier()).graph().delete(((DeleteGraphCommand)subCommand).getGraph());
        } else if(subCommand instanceof DeleteUserCommand) {
            serverAccountManager.delete(((DeleteUserCommand)subCommand).getProfile());
        }
    }


    @Override
    public Class<?> processingClass() {
        return DeleteCommand.class;
    }
    
}
