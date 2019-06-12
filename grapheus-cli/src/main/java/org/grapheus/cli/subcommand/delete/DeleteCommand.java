/**
 * 
 */
package org.grapheus.cli.subcommand.delete;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.spi.SubCommand;
import org.kohsuke.args4j.spi.SubCommandHandler;
import org.kohsuke.args4j.spi.SubCommands;
import org.grapheus.cli.subcommand.GrapheusCLICommand;

import lombok.Getter;

/**
 * @author black
 *
 */
@Getter
public class DeleteCommand implements GrapheusCLICommand {

    @Argument(required = true, index = 0, metaVar = "action", usage = "", handler = SubCommandHandler.class)
    @SubCommands({
        @SubCommand(name = "graph", impl = DeleteGraphCommand.class),
        @SubCommand(name = "user", impl = DeleteUserCommand.class)
    })
    private GrapheusCLICommand command;
    
}
