/**
 * 
 */
package grapheus.persistence.model.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.arangodb.entity.DocumentField;
import com.arangodb.entity.DocumentField.Type;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import grapheus.persistence.model.annotation.Index;
import grapheus.persistence.model.annotation.Index.IndexType;
import grapheus.view.SemanticFeature;

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
@Index(fields="artifact.updatedTimestamp", type=IndexType.SKIP, unique=false)
@Index(fields=PersistentVertex.FIELD_VIEW_HINT_VAL, type=IndexType.HASH, unique=false)
@Index(fields=PersistentVertex.FIELD_VIEW_HINT_TYPE, type=IndexType.HASH, unique=false) //It has low selectivity, maybe not worth to spend memory
@Index(fields=PersistentVertex.FIELD_TITLE, type=IndexType.SKIP, unique=false)
@ToString(of={"id"})
public class PersistentVertex {
    public static final String FIELD_ID = "_key";
    public static final String FIELD_SEMANTIC_FEATURES = "semanticFeatures";
    public static final String FIELD_VIEW_HINT_TYPE = "semanticFeatures[*].feature";
    public static final String FIELD_VIEW_HINT_VAL = "semanticFeatures[*].value";

    public static final String FIELD_TITLE = "title";
    public static final String FIELD_ARTIFACT_UPDATED = "updatedTimestamp";
    public static final String FIELD_TASK_IDS = "taskIds";
    public static final String FIELD_ARTIFACT_PROCESSED = "processedTimestamp";
    public static final String DESCRIPTION = "description";
    public static final String VIRTUAL_ORDER = "order";
    
    @DocumentField(Type.KEY)
    private String id;

    @DocumentField(Type.REV)
    private String rev;
    
    /** External local references to other artifacts. For example, JIRA issue key or URL */
    private List<SemanticFeature> semanticFeatures;
    
    private Long processedTimestamp;
    
    private Long updatedTimestamp;
    
    private Long createdTimestamp;
    
    /** References to the distance threshold tags */
    private Set<String> taskIds;
    
    private String url;
    
    private String title;
    
    private String description;
    
    private String sourceId;

    private List<String> tags;
    
    public static class PersistentVertexBuilder {
        private List<SemanticFeature> semanticFeatures = new ArrayList<>();
    }
}
