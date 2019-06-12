/**
 * 
 */
package org.grapheus.cli.subcommand.processor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.grapheus.cli.AccountSpecLoader;
import org.grapheus.cli.model.CLIAccount;
import org.grapheus.cli.subcommand.GrapheusCLICommand;
import org.grapheus.cli.subcommand.ProfileCommand;
import org.grapheus.common.BasicAuthUtil;

import lombok.RequiredArgsConstructor;

/**
 * @author black
 *
 */
@RequiredArgsConstructor
//TODO: deprecate/remove this class?
public abstract class AbstractProfileCommandProcessor implements CommandProcessor {
    private final AccountSpecLoader accountSpecLoader;    

    @Override
    public void process(GrapheusCLICommand command) throws CommandProcessingException {
        ProfileCommand profileCommand = (ProfileCommand) command;
        
        CLIAccount account;
        if(profileCommand.getFile() != null) {
            Path profileFile = Paths.get(profileCommand.getFile());
            try {
                account = accountSpecLoader.loadAccount(Files.newInputStream(profileFile));
            } catch (IOException e) {
                throw new CommandProcessingException("Cannot load profile", e);
            }
        } else {
            account = CLIAccount.builder().//
                    accontName(profileCommand.getUserName() == null ? null : profileCommand.getUserName()).//
                    password(profileCommand.getPassword() == null ? null : BasicAuthUtil.fromBase64(profileCommand.getPassword())).//
                    build();
        }
        process(account, profileCommand);
    }


    protected abstract void process(CLIAccount account, ProfileCommand createCommand);
    
}
