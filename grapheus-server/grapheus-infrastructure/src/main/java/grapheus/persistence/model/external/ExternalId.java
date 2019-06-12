/**
 * 
 */
package grapheus.persistence.model.external;

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
public class ExternalId {
    
    /** Global data source id */
    private String dataSourceId;
    
    /** Id of the item inside of the data source */
    private String id;

}
