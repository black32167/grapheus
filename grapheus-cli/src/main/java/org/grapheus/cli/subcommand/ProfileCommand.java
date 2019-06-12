/**
 * 
 */
package org.grapheus.cli.subcommand;

import org.kohsuke.args4j.Option;

import lombok.Getter;

/**
 * @author black
 *
 */
@Getter
public class ProfileCommand implements GrapheusCLICommand {
    @Option(name="-f", aliases="--file")
    private String file;
    
    @Option(name="-u", aliases="--user")
    private String userName;
    
    @Option(name="-p", aliases="--password")
    private String password;
}
