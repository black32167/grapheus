/**
 * 
 */
package grapheus.persistence.model.vocabulary;

import com.arangodb.entity.DocumentField;
import com.arangodb.entity.DocumentField.Type;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import grapheus.persistence.model.annotation.Entity;
import grapheus.persistence.model.annotation.Index;

/**
 * @author black
 *
 */
@Entity(name = "VOCABULARY")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(of={"scope", "term"})
@Index(fields = {"scope", "term"})
@Index(fields = "scope", unique=false)
public class VocabularyTerm {
    public final static String FIELD_TERM = "term";
    public final static String FIELD_COUNT = "mentionsCount";
    public static final String FIELD_SCOPE = "scope";
    
    @DocumentField(Type.KEY)
    private String id;
    
    private String term;
    
    @DocumentField(Type.REV)
    private String rev;
    
    @NonNull
    private String scope;
    
    private int mentionsCount;
}
