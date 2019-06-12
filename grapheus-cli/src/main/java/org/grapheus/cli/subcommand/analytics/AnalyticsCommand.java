/**
 * 
 */
package org.grapheus.cli.subcommand.analytics;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;
import org.grapheus.cli.subcommand.AbstractGraphOperationCommand;

import lombok.Getter;

/**
 * @author black
 *
 */
@Getter
public class AnalyticsCommand extends AbstractGraphOperationCommand {
    //@Option()
    @Argument
    private String operation;
    
    @Argument(index=1)
    private String parameters;
    
    @Option(name="-p")
    private String pattern;
    

}
