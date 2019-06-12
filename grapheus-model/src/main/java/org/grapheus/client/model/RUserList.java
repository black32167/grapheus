/**
 * 
 */
package org.grapheus.client.model;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author black
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RUserList implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<RUser> users;
}
