/**
 * 
 */
package org.grapheus.cli.subcommand.delete;

import org.kohsuke.args4j.Option;
import org.grapheus.cli.subcommand.GrapheusCLICommand;

import lombok.Getter;

/**
 * @author black
 *
 */
@Getter
public class DeleteUserCommand implements GrapheusCLICommand {

    @Option(name="-u", aliases="--user")
    private String profile;
    
}
