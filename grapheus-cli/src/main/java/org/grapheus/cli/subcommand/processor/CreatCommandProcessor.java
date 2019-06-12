/**
 * 
 */
package org.grapheus.cli.subcommand.processor;

import javax.inject.Inject;

import org.grapheus.cli.model.CLIAccount;
import org.grapheus.cli.remote.ServerAccountManager;
import org.grapheus.cli.security.CLIUserContext;
import org.grapheus.cli.subcommand.GrapheusCLICommand;
import org.grapheus.cli.subcommand.create.CreateCommand;
import org.grapheus.cli.subcommand.create.CreateGraphCommand;
import org.grapheus.cli.subcommand.create.CreateUserCommand;
import org.grapheus.client.GrapheusClientFactory;
import org.grapheus.common.BasicAuthUtil;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * @author black
 *
 */
@Service
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class CreatCommandProcessor implements CommandProcessor {
    private final ServerAccountManager serverAccountManager;
    private final GrapheusClientFactory rcFactory;
    private final CLIUserContext userCtx;

    @Override
    public Class<?> processingClass() {
        return CreateCommand.class;
    }

    @Override
    public void process(GrapheusCLICommand createCommand) throws CommandProcessingException {
        GrapheusCLICommand command = ((CreateCommand) createCommand).getSubCommand();
        if(command instanceof CreateGraphCommand) {
            rcFactory.forUser(
                    userCtx.credentialsSupplier())
                        .operation()
                        .generateEmptyGraph(
                                ((CreateGraphCommand)command).getGraph());
        } else if (command instanceof CreateUserCommand) {
            CreateUserCommand createUserCommand = (CreateUserCommand) command;
            CLIAccount account = CLIAccount.builder().//
                    accontName(createUserCommand.getUserName() == null ? null : createUserCommand.getUserName()).//
                    password(createUserCommand.getPassword() == null ? null : BasicAuthUtil.fromBase64(createUserCommand.getPassword())).//
                    build();
            serverAccountManager.create(account, false);
        }
    }

}
