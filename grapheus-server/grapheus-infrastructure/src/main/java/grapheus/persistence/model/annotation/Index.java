/**
 * 
 */
package grapheus.persistence.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies fields should be indexed;
 * Index is the UNIQ by default
 * 
 * @author black
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(Indexes.class)
public @interface Index {
    enum IndexType {
        HASH,SKIP
    }
    String[] fields();

    boolean unique() default true;
    IndexType type() default IndexType.HASH;
}
