/**
 * 
 */
package org.grapheus.cli.subcommand;

import org.kohsuke.args4j.Option;
import org.grapheus.client.model.graph.GraphNamesConstants;

import lombok.Getter;

/**
 * @author black
 *
 */
@Getter
public abstract class AbstractGraphOperationCommand implements GrapheusCLICommand {
    @Option(name="-g", aliases="--graph", required=false)
    private String graph = GraphNamesConstants.DEFAULT_GRAPH_NAME;
}
