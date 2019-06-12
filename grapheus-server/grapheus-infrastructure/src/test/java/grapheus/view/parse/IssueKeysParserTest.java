/**
 * 
 */
package grapheus.view.parse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

/**
 * @author black
 *
 */
public class IssueKeysParserTest {
    @Test
    public void testKeysAbsent() {
        List<String> keys = LocalKeysParser.findAllKnownIdentifiers(" blah ");
        assertEquals(0, keys.size());
    }
    
    @Test
    public void testKeysPresent() {
        List<String> keys = LocalKeysParser.findAllKnownIdentifiers("PLA-0 blah PLA-1 ");
        assertEquals(2, keys.size());
    }
    
    @Test
    public void testEmpty() {
        List<String> keys = LocalKeysParser.findAllKnownIdentifiers("");
        assertEquals(0, keys.size());
    }
    
   
    public void testNull() {
        List<String> keys = LocalKeysParser.findAllKnownIdentifiers(null);
        assertTrue(keys.isEmpty());
    }

}
