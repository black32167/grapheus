/**
 * 
 */
package grapheus.service.concurrency;

import java.io.IOException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import grapheus.concurrency.ThreadLimitsProviderImpl;
import grapheus.server.config.ConcurrencyBackendConfig;

/**
 * @author black
 */
public class ThreadLimitsProviderImplTest {
	@Mock
	private ConcurrencyBackendConfig config;
	
    @Test
    public void testCustomLimit() throws IOException {
        ThreadLimitsProviderImpl provider = getProvider();

        Assert.assertEquals(10, provider.getMaxThreads("some.thread.group.max"));
    }
    
    @Test
    public void testDefaultLimit() throws IOException {
        ThreadLimitsProviderImpl provider = getProvider();

        Assert.assertEquals(5, provider.getMaxThreads("some.other.thread.group.max"));
    }
    
    private ThreadLimitsProviderImpl getProvider() throws IOException {
    	
        URL propertiesLocation = getClass().getResource("ThreadLimitsProviderImplTest.properties");//new ByteArrayInputStream("some.thread.group.max=10".getBytes());
        Mockito.when(config.getExecutorsConfigUrl()).thenReturn(propertiesLocation);
        Mockito.when(config.getDefaultThreadLimit()).thenReturn(5);
        
        ThreadLimitsProviderImpl provider = new ThreadLimitsProviderImpl(config);
        provider.init();
        return provider;
    }

}
