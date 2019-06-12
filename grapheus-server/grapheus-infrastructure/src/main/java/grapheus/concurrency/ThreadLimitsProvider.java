/**
 * 
 */
package grapheus.concurrency;

/**
 * @author black
 *
 */
public interface ThreadLimitsProvider {
    int getMaxThreads(String runnerName);
}
