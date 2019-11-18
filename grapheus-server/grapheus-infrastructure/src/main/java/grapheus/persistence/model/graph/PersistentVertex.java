/**
 * 
 */
package grapheus.persistence.model.graph;

import com.arangodb.entity.DocumentField;
import com.arangodb.entity.DocumentField.Type;
import grapheus.persistence.model.annotation.Index;
import grapheus.persistence.model.annotation.Index.IndexType;
import grapheus.view.SemanticFeature;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Persistent representation of the issue
 * 
 * @author black
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of="id")
//@Index(fields=PersistentVertex.FIELD_VIEW_HINT_VAL, type=IndexType.HASH, unique=false)
//@Index(fields=PersistentVertex.FIELD_VIEW_HINT_TYPE, type=IndexType.HASH, unique=false) //It has low selectivity, maybe not worth to spend memory
@Index(fields=PersistentVertex.FIELD_TITLE, type=IndexType.SKIP, unique=false)
@ToString(of={"id"})
public class PersistentVertex {
    public static final String FIELD_ID = "_key";
    public static final String FIELD_SEMANTIC_FEATURES = "semanticFeatures";

    public static final String FIELD_TITLE = "title";
    public static final String FIELD_ARTIFACT_UPDATED = "updatedTimestamp";
    public static final String FIELD_ARTIFACT_PROCESSED = "processedTimestamp";
    public static final String DESCRIPTION = "description";
    public static final String VIRTUAL_ORDER = "order";

    @DocumentField(Type.KEY)
    private String id;

    @DocumentField(Type.REV)
    private String rev;

    /** External local references to other artifacts. For example, JIRA issue key or URL */
    private Map<String, SemanticFeature> semanticFeatures;

    private Long processedTimestamp;
    private Long updatedTimestamp;
    private Long createdTimestamp;
    private String url;
    private String title;
    private String description;
    private String sourceId;
    private List<String> tags;
    private String generativeValue;
    
    public static class PersistentVertexBuilder {
        private Map<String, SemanticFeature> semanticFeatures = new HashMap<>();
    }
}
