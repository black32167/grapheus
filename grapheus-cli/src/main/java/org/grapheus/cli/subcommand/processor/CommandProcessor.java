/**
 * 
 */
package org.grapheus.cli.subcommand.processor;

import org.grapheus.cli.subcommand.GrapheusCLICommand;

/**
 * @author black
 *
 */
public interface CommandProcessor {
    void process(GrapheusCLICommand command) throws CommandProcessingException;
    Class<?> processingClass();
}
