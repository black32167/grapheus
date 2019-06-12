/**
 * 
 */
package grapheus.persistence.datasource;

/**
 * @author black
 *
 */
public interface ClosableDataSource<T> extends AutoCloseable, Iterable<T> {
    void close();
}
