/**
 * 
 */
package grapheus.persistence.model.personal;

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
@Entity(name = "USER")

//TODO: implement 'admin' flag
public class GrapheusUser {
    public static final String FIELD_ID = "_key";

    @DocumentField(Type.REV)
    private String rev;

    @DocumentField(Type.KEY)
    private String name;

    private byte[] hash;

    private long creationTimestamp;

}
