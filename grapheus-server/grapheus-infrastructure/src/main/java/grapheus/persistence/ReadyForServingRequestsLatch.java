/**
 * 
 */
package grapheus.persistence;

/**
 * Supposed to block thread till persistence is not fully initialized.
 * 
 * @author black
 */
public interface ReadyForServingRequestsLatch {
    void waitUntilReady();
}
