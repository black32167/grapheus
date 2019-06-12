/**
 * 
 */
package grapheus.persistence.model.common.creds;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Handy credentials holder solving lack of polymorphism in ArangoDB
 * 
 * @author black
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CredentialsHolder {

    private DSOAuthCredentials oAuthCredentials;
    private DSBasicCredentials basicUserCredentials;
    private DSJWTCredentials jwtCredentials;

    private final static List<Field> fields = new ArrayList<>();
    static {
        for(Field f: CredentialsHolder.class.getDeclaredFields()) {
            fields.add(f);
         }
    }
    
    /**
     * Returns polymorphic credentials.
     */
    public DSCredentials get() {
        for(Field f:fields) {
            if((f.getModifiers() & Modifier.STATIC) != 0) {
                continue;
            }
            Object v;
            try {
                v = f.get(this);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if(v != null) {
                return (DSCredentials) v;
            }
        }
        
        return null;
    }
    

    public void set(DSCredentials value) {
        Class<?> valueClass = value == null ? null : value.getClass();
        for(Field f:fields) {
            Class<?> fieldType = f.getType();
            if((f.getModifiers() & Modifier.STATIC) != 0) {
                continue;
            }
            try {
                f.set(this, fieldType == valueClass ? value : null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        
    }

}
