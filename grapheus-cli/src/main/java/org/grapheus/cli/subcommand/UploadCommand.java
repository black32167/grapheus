/**
 * 
 */
package org.grapheus.cli.subcommand;

import org.kohsuke.args4j.Option;

import lombok.Data;
/**
 * @author black
 *
 */
@Data
public class UploadCommand extends AbstractGraphOperationCommand {
    @Option(name="-p", aliases="--path")
    private String path;
}
