/**
 * 
 */
package org.grapheus.web;

import static org.grapheus.web.AuthUtil.getPassword;
import static org.grapheus.web.AuthUtil.getUserName;

import org.grapheus.client.UserClient;
import org.grapheus.client.api.GraphAPI;
import org.grapheus.client.api.OperationAPI;
import org.grapheus.client.api.TelemetryAPI;
import org.grapheus.client.api.UserCreationAPI;
import org.grapheus.client.api.UserManagementAPI;
import org.grapheus.client.api.VertexAPI;
import org.grapheus.client.http.auth.GrapheusClientCredentials;
/**
 * @author black
 *
 */
public final class RemoteUtil {

    public static UserCreationAPI userCreationAPI() {
        return GrapheusClientFactoryHolder.grapheusFactory().userCreator();
    }
    
    public static UserManagementAPI userAPI() {
        return userClient().user();
    }
    
    public static UserClient userClient() {
        return GrapheusClientFactoryHolder.grapheusFactory().forUser(
                () -> new GrapheusClientCredentials(getUserName(), getPassword()));
    }
    
    public static UserClient contextlessUserClient() {
        String userName = getUserName();
        byte[] password = getPassword();
        return GrapheusClientFactoryHolder.grapheusFactory().forUser(
                () -> new GrapheusClientCredentials(userName, password));
    }

    public static GraphAPI graphsAPI() {
        return userClient().graph();
    }
    

    public static OperationAPI operationAPI() {
        return userClient().operation();
    }
    
    public static VertexAPI vertexAPI() {
        return userClient().vertex();
    }

    public static TelemetryAPI telemetryAPI() {
        return GrapheusClientFactoryHolder.grapheusFactory().telemetry();
    }
    
}
