/**
 * 
 */
package grapheus.view.extract.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

/**
 * @author black
 *
 */
@Service
public class SimpleStopWordRegistry implements StopWordRegistry {
    private static final Set<String> HARDCODED_STOP_WORDS = new HashSet<String>(Arrays.asList(
            "and","or","a","the", "com", "lt", "gt", "this", "add", "img", "when", "tt", "not", "https", "http", "exception",
            "ref", "nofollow"
            ));

    @Override
    public boolean isStopWord(String word) {
        return HARDCODED_STOP_WORDS.contains(word);
    }

}
