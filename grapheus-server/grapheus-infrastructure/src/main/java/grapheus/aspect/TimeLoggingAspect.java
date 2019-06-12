/**
 * 
 */
package grapheus.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author black
 */
@Aspect
@Component
@Slf4j
public class TimeLoggingAspect {
    
    @Pointcut("@annotation(grapheus.aspect.LogTime)")
    public void anyAnnotated() {}
    
    @Around("anyAnnotated()")
    public Object onStorageException(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long end = System.currentTimeMillis();
        log.debug("Operation " + joinPoint.getSignature().getName() + " took " + (end-start) + " ms.");
        
        return result;
    }
}
