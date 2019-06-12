/**
 * 
 */
package grapheus.persistence.model.common.creds;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author black
 *
 */
public class CredentialsHolderTest {
    
    @Test
    public void testSpecificFieldSetGetOverride() {
        CredentialsHolder ch = new CredentialsHolder();
        ch.set(new DSBasicCredentials());
        DSCredentials readCreds = ch.get();
        Assert.assertEquals(DSBasicCredentials.class, readCreds.getClass());
        
        ch.set(new DSJWTCredentials());
        readCreds = ch.get();
        Assert.assertEquals(DSJWTCredentials.class, readCreds.getClass());
        
    }
    

    @Test
    public void testNull() {
        CredentialsHolder ch = new CredentialsHolder();
        ch.set(new DSBasicCredentials());
        DSCredentials readCreds = ch.get();
        Assert.assertEquals(DSBasicCredentials.class, readCreds.getClass());
        
        ch.set(null);
        readCreds = ch.get();
        Assert.assertNull(readCreds);
        
    }

}
