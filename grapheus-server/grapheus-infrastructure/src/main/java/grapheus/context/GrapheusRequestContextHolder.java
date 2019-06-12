/**
 * 
 */
package grapheus.context;

import grapheus.security.RequestContext;

/**
 * @author black
 *
 */
public class GrapheusRequestContextHolder {
    private final static ThreadLocal<RequestContext> keyHolder = new ThreadLocal<>();
    
    public static void setContext(RequestContext ctx) {
        keyHolder.set(ctx);
        MDCManager.setMDCContext(ctx);
    }
    
    public static RequestContext getContext() {
        return keyHolder.get();
    }
}
