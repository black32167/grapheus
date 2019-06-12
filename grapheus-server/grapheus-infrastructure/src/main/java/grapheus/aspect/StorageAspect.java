/**
 * 
 */
package grapheus.aspect;

import javax.annotation.PreDestroy;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import grapheus.exception.ShutdownException;
import grapheus.persistence.exception.DocumentsConflictException;

/**
 * @author black
 */
@Aspect
@Component
@Slf4j
public class StorageAspect {
    private volatile boolean shutdown;
    
    @Pointcut("within(@org.springframework.stereotype.Repository *)")
    public void anyStorage() {}
    
    @PreDestroy
    void onDestroy() {
        shutdown = true;
    }

    @Around("anyStorage()")
    public Object onStorageException(ProceedingJoinPoint joinPoint) throws Throwable {
        while (!shutdown) {
            try {
                return joinPoint.proceed();
            } catch (DocumentsConflictException e) {
                // Ignore and allow retry again...
                log.debug("Document update conflict", e);
            }
          
        };
        throw new ShutdownException();
       
    }
}
