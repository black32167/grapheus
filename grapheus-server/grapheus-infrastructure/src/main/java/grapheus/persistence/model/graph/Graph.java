/**
 * 
 */
package grapheus.persistence.model.graph;

import com.arangodb.entity.DocumentField;
import com.arangodb.entity.DocumentField.Type;
import grapheus.persistence.model.annotation.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author black
 */
@Entity(name = "graphMeta")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Graph {
    public final static String FIELD_PROCESSED_TIMESTAMP = "processedTimestamp";

    public static final String FIELD_USER_KEYS = "userKeys";

    public static final String FIELD_NAME = "_key";

    public static final String FIELD_PUBLIC = "publicAccess";
    
    @DocumentField(Type.KEY)
    private String name;
    
    @DocumentField(Type.REV)
    private String rev;
    
    private Long processedTimestamp;
    
    private boolean publicAccess;
    private List<String> userKeys;
    private List<String> operationsApplied;
    private String sourceGraphId;
    private String generativePropertyName;
}
