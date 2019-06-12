/**
 * 
 */
package org.grapheus.cli.subcommand.processor;

import javax.inject.Inject;

import org.grapheus.cli.AccountSpecLoader;
import org.grapheus.cli.model.CLIAccount;
import org.grapheus.cli.remote.ServerAccountManager;
import org.grapheus.cli.subcommand.ProfileCommand;
import org.grapheus.cli.subcommand.RecreateProfile;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * @author black
 *
 */
@Service
@Slf4j
public class RecreateProfieCommandProcessor extends AbstractProfileCommandProcessor {
    private final ServerAccountManager serverAccountManager;

    @Inject
    public RecreateProfieCommandProcessor(AccountSpecLoader accountSpecLoader,
            ServerAccountManager serverAccountManager) {
        super(accountSpecLoader);
        this.serverAccountManager = serverAccountManager;
    }

    @Override
    public Class<?> processingClass() {
        return RecreateProfile.class;
    }


    @Override
    protected void process(CLIAccount account, ProfileCommand createCommand) {
        serverAccountManager.create(account, true);
    }

}
