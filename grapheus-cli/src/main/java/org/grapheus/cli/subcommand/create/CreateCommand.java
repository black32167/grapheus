/**
 * 
 */
package org.grapheus.cli.subcommand.create;

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
public class CreateCommand implements GrapheusCLICommand {
    
    @Argument(required = true, index = 0, metaVar = "action", usage = "", handler = SubCommandHandler.class)
    @SubCommands({
        @SubCommand(name = "graph", impl = CreateGraphCommand.class),
        @SubCommand(name = "user", impl = CreateUserCommand.class)
    })
    private GrapheusCLICommand subCommand;
    
}
