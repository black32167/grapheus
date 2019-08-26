package grapheus.persistence.storage.graph.transaction;

import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocystream.Request;
import com.arangodb.velocystream.RequestType;
import com.arangodb.velocystream.Response;
import grapheus.persistence.conpool.DBConnectionPool;
import org.grapheus.common.HttpCodes;

import javax.inject.Inject;
import java.util.Map;

abstract public class FoxxSupport {
    @Inject
    private DBConnectionPool dbTemplate;

    protected <T> T invokeFoxx(String functionName, Map<String, Object> parameters, Class<T> returnType) throws FoxxException {
       return dbTemplate.query(db-> {
            Request request = new Request(db.name(), RequestType.GET, "/grapheus/"+functionName);
            for(Map.Entry<String, Object> e: parameters.entrySet()) {
                request.putQueryParam(e.getKey(), e.getValue());
            }
            Response response = db.arango().execute(request);
            int rCode = response.getResponseCode();
            if(rCode != HttpCodes.NO_CONTENT && rCode != HttpCodes.OK) {
                throw new FoxxException("Error invoking remote service (code " + response.getResponseCode() + ")");
            }
            VPackSlice responseBody = response.getBody();
            return responseBody == null  //
                    ? null //
                    : db.util().deserialize(responseBody, returnType);
        });
    }
}
