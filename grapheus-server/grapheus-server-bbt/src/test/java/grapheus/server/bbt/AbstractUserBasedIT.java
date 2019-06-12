/**
 * 
 */
package grapheus.server.bbt;

import java.util.UUID;

import org.junit.Before;
import org.grapheus.client.http.GrapheusRestClient;
import org.grapheus.client.http.auth.GrapheusClientCredentials;

import lombok.Getter;
import grapheus.server.bbt.util.UserTestTemplate;
import grapheus.server.bbt.util.UserTestTemplate.UserTestTemplateCallback;

/**
 * @author black
 *
 */
public class AbstractUserBasedIT {
    @Getter
    private String currentScope;
    
    @Before
    public void init() {
        currentScope = UUID.randomUUID().toString();
    }

    private UserTestTemplate grapheusTestTemplate = new UserTestTemplate();
    
    protected void withUser(String userKey, UserTestTemplateCallback userConsumer) {
        grapheusTestTemplate.withUser(userKey, userConsumer, true);
    }
    
    protected void withUser(UserTestTemplateCallback userConsumer) {
        grapheusTestTemplate.withUser(userConsumer);
    }
    
    protected void withUser(UserTestTemplateCallback userConsumer, boolean delete) {
        grapheusTestTemplate.withUser(userConsumer, delete);
    }
    
    protected GrapheusRestClient getClient(String userKey, GrapheusClientCredentials credentials) {
        return grapheusTestTemplate.getClient(userKey, credentials);
    }
}
