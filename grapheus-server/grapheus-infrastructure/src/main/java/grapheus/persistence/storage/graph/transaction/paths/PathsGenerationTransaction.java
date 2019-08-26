/**
 * 
 */
package grapheus.persistence.storage.graph.transaction.paths;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.arangodb.internal.util.ArangoSerializationFactory;
import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocystream.Request;
import com.arangodb.velocystream.RequestType;
import grapheus.persistence.conpool.DBConnectionPool;
import grapheus.persistence.storage.graph.transaction.FoxxEndpointNames;
import grapheus.persistence.storage.graph.transaction.FoxxSupport;
import org.springframework.stereotype.Service;

import com.arangodb.model.TransactionOptions;

import grapheus.persistence.storage.graph.GraphNameUtils;
import grapheus.persistence.storage.graph.transaction.ServerSideTransaction;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

/**
 * @author black
 */
@Service
public class PathsGenerationTransaction extends FoxxSupport {
    public boolean findPaths(String sourceGraphName, String newGraphName,
            Collection<String> boundaryVerticesIds) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("graphId", sourceGraphName);
        parameters.put("newGraphId", newGraphName);
        parameters.put("boundaryVerticesIds",  String.join(",", boundaryVerticesIds));

        invokeFoxx(FoxxEndpointNames.FIND_PATHS, parameters, String.class);
        return true;
    }
}
