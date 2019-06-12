/**
 * 
 */
package org.grapheus.client;

import org.grapheus.client.api.ComputeAPI;
import org.grapheus.client.api.DataStatisticsAPI;
import org.grapheus.client.api.GraphAPI;
import org.grapheus.client.api.OperationAPI;
import org.grapheus.client.api.UserManagementAPI;
import org.grapheus.client.api.VertexAPI;
import org.grapheus.client.http.GrapheusRestClient;

import lombok.RequiredArgsConstructor;

/**
 * @author black
 *
 */
@RequiredArgsConstructor
public class UserClient {
    private final GrapheusRestClient restClient;
    
    public UserManagementAPI user() {
        return new UserManagementAPI(restClient);
    }

    public GraphAPI graph() {
        return new GraphAPI(restClient);
    }

    public VertexAPI vertex() {
        return new VertexAPI(restClient);
    }

    public ComputeAPI analytics() {
        return new ComputeAPI(restClient);
    }
    
    public DataStatisticsAPI dataStat() {
        return new DataStatisticsAPI(restClient);
    }

    public OperationAPI operation() {
        return new OperationAPI(restClient);
    }


}
