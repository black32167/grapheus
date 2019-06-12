/**
 * 
 */
package grapheus.context;

import org.slf4j.MDC;

import grapheus.security.RequestContext;

/**
 * @author black
 *
 */
public class MDCManager {
    private static final String MDC_USER = "USER";
    private static final String MDC_URL = "URL";

    public static void setMDCContext(RequestContext ctx) {
        if(ctx != null) {
            MDC.put(MDC_USER, ctx.getUserId());
            MDC.put(MDC_URL, ctx.getRequestUrl().replaceAll("\\?.*",""));
        } else {
            MDC.remove(MDC_USER);
            MDC.remove(MDC_URL);
        }
    }
}
