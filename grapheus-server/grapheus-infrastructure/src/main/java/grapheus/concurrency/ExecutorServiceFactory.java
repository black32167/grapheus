/**
 * 
 */
package grapheus.concurrency;

import static org.grapheus.common.concurrent.ConcurrentUtils.daemon;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import lombok.RequiredArgsConstructor;
import grapheus.context.GrapheusRequestContextHolder;
import grapheus.security.RequestContext;

/**
 * @author black
 */
@Service
@RequiredArgsConstructor(onConstructor= @__({@Inject}))
public class ExecutorServiceFactory {
    
    private final ThreadLimitsProvider limitsProvider;
    
    private final ConcurrentMap<String, ExecutorService> executors = new ConcurrentHashMap<>();
    
    public <T> ListenableFuture<T> runDaemon(String name, Callable<T> r) {
        return (ListenableFuture<T>) executors.computeIfAbsent(name, this::createExecutor).//
                submit(contextAwareCallable(r));
    }
    
    public ListenableFuture<?> runDaemon(String name, Runnable r) {
        return (ListenableFuture<?>) executors.computeIfAbsent(name, this::createExecutor).//
                submit(contextAwareRunnable(r));
    }

    public void periodic(String name, Runnable r, long period) {
        ((ScheduledExecutorService)executors.computeIfAbsent(name, this::createTimedExecutor)).//
                scheduleAtFixedRate(r, 0, period, TimeUnit.MILLISECONDS);
    }
    
    public <V> Callable<V> contextAwareCallable(Callable<V> r) {
        RequestContext requestContext = GrapheusRequestContextHolder.getContext();
        return () -> {
            GrapheusRequestContextHolder.setContext(requestContext);
            return r.call();
        };
    }
    
    public Runnable contextAwareRunnable(Runnable r) {
        RequestContext requestContext = GrapheusRequestContextHolder.getContext();
        return () -> {
            GrapheusRequestContextHolder.setContext(requestContext);
            r.run();
        };
    }
    
    private ScheduledExecutorService createTimedExecutor(String name) {
        return Executors.newScheduledThreadPool(limitsProvider.getMaxThreads(name), daemon(name));
    }
    private ListeningExecutorService createExecutor(String name) {
        return MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(limitsProvider.getMaxThreads(name), daemon(name)));
    }

}
