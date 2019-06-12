/**
 * 
 */
package grapheus.persistence.graph.calculate;

import lombok.Data;

/**
 * @author black
 *
 */
@Data
public class CalculatedVertexInfo {
    private String vertexId;
    private String stringValue;
    private Integer intValue;
    
    public String getSerializedInfo() {
        if(intValue != null) {
            return intValue.toString();
        }
        return stringValue;
    }
}
