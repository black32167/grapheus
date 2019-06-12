/**
 * 
 */
package grapheus.persistence.model;

import java.io.Serializable;

import com.arangodb.entity.DocumentField;
import com.arangodb.entity.DocumentField.Type;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import grapheus.persistence.model.annotation.Entity;

/**
 * @author black
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Deprecated // Maybe we do not need this anymore?
@Entity(name="sequence")
public class SequenceState implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String SEQUENCE_COLLECTION = "sequence";

    public static final String SEQ_NAME = "_key";
    
    @DocumentField(Type.ID)
    protected String id;
    
    @DocumentField(Type.KEY)
    protected String sequence;
    
    @DocumentField(Type.REV)
    protected String rev;
    
    private long value;
    
}
