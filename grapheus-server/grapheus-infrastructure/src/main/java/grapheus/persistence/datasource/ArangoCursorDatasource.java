/**
 * 
 */
package grapheus.persistence.datasource;

import java.io.IOException;
import java.util.Iterator;
import java.util.function.Function;

import com.arangodb.ArangoCursor;

import lombok.RequiredArgsConstructor;

/**
 * @author black
 */
@RequiredArgsConstructor
public class ArangoCursorDatasource<T, R> implements ClosableDataSource<R>{
    private final ArangoCursor<T> cursor;
    private final Function<T, R> transformer;

    @Override
    public void close() {
        try {
            cursor.close();
        } catch (IOException e) {}
    }

    public static <T> ArangoCursorDatasource<T, T> from(ArangoCursor<T> ret) {
        return new ArangoCursorDatasource<T, T>(ret, Function.identity());
    }
    
    public static <T, R> ArangoCursorDatasource<T, R> from(ArangoCursor<T> ret, Function<T, R> transformer) {
        return new ArangoCursorDatasource<T, R>(ret, transformer);
    }

    @Override
    public Iterator<R> iterator() {
        return new Iterator<R>() {
            @Override
            public boolean hasNext() {
                return cursor.hasNext();
            }

            @Override
            public R next() {
                return transformer.apply(cursor.next());
            }
        };
    }
}
