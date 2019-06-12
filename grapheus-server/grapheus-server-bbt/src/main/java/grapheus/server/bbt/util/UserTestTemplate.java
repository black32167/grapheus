/**
 * 
 */
package grapheus.server.bbt.util;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import org.grapheus.client.http.GrapheusRestClient;
import org.grapheus.client.http.auth.GrapheusClientCredentials;
import org.grapheus.client.model.RUser;
import org.grapheus.client.model.security.RUserCredentials;

/**
 * @author black
 */
public class UserTestTemplate {
    @FunctionalInterface
    public interface UserTestTemplateCallback {
        void doWithUser(String userId, GrapheusRestClient client);
    }
    private static final byte[] PASSWORD = "12345".getBytes(StandardCharsets.UTF_8);
    
    private String baseURL;

    private GrapheusClientCredentials currentCredentials;

    public UserTestTemplate() {
        this(Optional.ofNullable(
                System.getProperty("test.rest.url", "http://127.0.0.1:8081/grapheus")).//
                orElseThrow(() -> new RuntimeException("test.rest.url not specified")));//, "http://127.0.0.1:8081/grapheus"));
    }

    public UserTestTemplate(String baseURL) {
        this.baseURL = baseURL;
    }

    public void withUser(UserTestTemplateCallback userConsumer) {
        withUser(userConsumer, true);
    }
    public void withUser(UserTestTemplateCallback userConsumer, boolean delete) {
        withUser(UUID.randomUUID().toString(), userConsumer, delete);
    }
    public void withUser(String userKey, UserTestTemplateCallback userConsumer, boolean delete) {
        
        RUser user = RUser.builder().name(userKey).password(PASSWORD).build();

        getClient(userKey, null).post("user", user, Void.class);
        
        this.currentCredentials = new GrapheusClientCredentials(userKey, PASSWORD);
        GrapheusRestClient restClient = getClient(userKey, currentCredentials);
        try {
            userConsumer.doWithUser(user.getName(), restClient);
        } finally {
            if (delete) {
                getClient(userKey, currentCredentials).delete("user/" + user.getName());   
            }
        }
    }
    
    public GrapheusRestClient getClient(String userKey, GrapheusClientCredentials credentials) {
        return new GrapheusRestClient(baseURL, () -> credentials) {

            @Override
            public void put(String resourcePath, Object entity) {
                if(entity instanceof RUserCredentials) {
                    currentCredentials = new GrapheusClientCredentials(userKey, ((RUserCredentials)entity).getUserSecret());
                }
                super.put(resourcePath, entity);
            }
            
        };
    }


}
