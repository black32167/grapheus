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
public class ListTasks extends AbstractGraphOperationCommand {
    @Option(name="-s", aliases="--scope")
    private String scope;
    
}
