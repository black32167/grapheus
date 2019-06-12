/**
 * 
 */
package org.grapheus.common.concurrent;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;

/**
 * @author black
 *
 */
@Slf4j
public final class ConcurrentUtils {

    public static <T> Stream<T> resolveFuture(Future<List<T>> future) {
        try {
            return future.get().stream();
        } catch (InterruptedException | ExecutionException e) {
            log.error("", e);
            return Stream.empty();
        }
    }
    public static <T> T resolveFutureSingle(Future<T> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("", e);
            throw new RuntimeException(e);
        }
    }
    public static ThreadFactory daemon(String name) {
        return new ThreadFactory() {
                AtomicInteger threadsCount = new AtomicInteger();
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r, name + threadsCount.incrementAndGet());
                    t.setDaemon(true);
                    return t;
                }
        };
    }
    private ConcurrentUtils() {}

}
