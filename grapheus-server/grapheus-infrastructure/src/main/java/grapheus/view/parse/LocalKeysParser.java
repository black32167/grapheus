/**
 * 
 */
package grapheus.view.parse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author black
 *
 */
public final class LocalKeysParser {
    private final static Pattern ISSUE_PATTERN = Pattern.compile("([A-Za-z]+-[0-9]+)[^0-9a-f-]");
    private final static Pattern UUID_PATTERN = Pattern.compile("([0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12})");

    public static List<String> findAllKnownIdentifiers(String textWithKeys) {
        if(textWithKeys == null){
            return Collections.emptyList();
        }
    
        List<String> keys = new ArrayList<String>();
        
        keys.addAll(findByPattern(textWithKeys, ISSUE_PATTERN));
        keys.addAll(findByPattern(textWithKeys, UUID_PATTERN));
       
        return keys;
    }
    public static List<String> findByPattern(String textWithKeys, Pattern pattern) {
        if(textWithKeys == null){
            return Collections.emptyList();
        }
        Matcher matcher = pattern.matcher(textWithKeys);
        List<String> keys = new ArrayList<String>();
        while(matcher.find()) {
            keys.add(matcher.group(1).toUpperCase());
        }
        return keys;
    }
}
