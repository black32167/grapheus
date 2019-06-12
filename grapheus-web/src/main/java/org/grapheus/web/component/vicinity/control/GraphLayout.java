/**
 * 
 */
package org.grapheus.web.component.vicinity.control;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author black
 *
 */
@RequiredArgsConstructor
@Getter
public enum GraphLayout {
    LAYERED("layered"), RADIAL("radial");
    
    private final String jsName;
}
