/**
 * 
 */
package org.grapheus.cli.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author black
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CLIAccount {
    private String accontName;
    private byte[] password;
}
