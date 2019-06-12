/**
 * 
 */
package org.grapheus.web.model;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

/**
 * @author black
 *
 */
@Builder
@Data
public class GraphInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    private String graphName;
    private boolean editPermitted;

}
