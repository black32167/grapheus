/**
 * 
 */
package grapheus.persistence.query;

import java.util.Collection;
import java.util.HashMap;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author black
 *
 */
public final class QueryUtil {
    public static String arrayParameter(String pPrefix, Collection<String> values, Map<String, Object> parametersMap) {
        Map<String, Object> p2v = new HashMap<>();
        
        int i = 0;
        for(String val: values) {
            p2v.put(pPrefix+i++, val);
        }
        parametersMap.putAll(p2v);
        return p2v.keySet().stream().map(p->"@"+p).collect(Collectors.joining(",", "[", "]"));
       
    }
    private QueryUtil(){}

}
