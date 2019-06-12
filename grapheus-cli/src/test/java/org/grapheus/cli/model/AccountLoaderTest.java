/**
 * 
 */
package org.grapheus.cli.model;

import java.io.IOException;
import java.io.InputStream;

import org.grapheus.cli.AccountSpecLoader;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author black
 *
 */
public class AccountLoaderTest {
    private final AccountSpecLoader accountLoader = new AccountSpecLoader();
    
    @Test
    public void testLoad() throws IOException {
        InputStream is = AccountLoaderTest.class.getResourceAsStream(
                AccountLoaderTest.class.getSimpleName() + ".yaml");
        CLIAccount account = accountLoader.loadAccount(is);
        Assert.assertEquals("Test Name", account.getAccontName());
      
    }
}
