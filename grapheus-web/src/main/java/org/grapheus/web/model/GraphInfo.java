/**
 * 
 */
package org.grapheus.web.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author black
 *
 */
@Builder
@Data
public class GraphInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String graphId;
    private boolean editPermitted;
    private String sourceGraphId;
    private String sourceGraphProperty;
}
