/**
 * 
 */
package org.grapheus.cli.subcommand.create;

import org.kohsuke.args4j.Option;
import org.grapheus.cli.subcommand.GrapheusCLICommand;

import lombok.Getter;

/**
 * @author black
 *
 */
@Getter
public class CreateUserCommand implements GrapheusCLICommand {
    @Option(name="-u", aliases="--user")
    private String userName;
    
    @Option(name="-p", aliases="--password")
    private String password;
}
