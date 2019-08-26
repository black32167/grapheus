package grapheus.persistence.foxx;

import com.arangodb.ArangoDatabase;
import grapheus.event.OnAfterDbConnectionListener;
import grapheus.server.config.DBConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

@Service
@Slf4j
public class GrapheusFoxxService implements OnAfterDbConnectionListener {
    private static final String NAME = "grapheus";
    private final WebTarget baseTarget;

    @Inject
    public GrapheusFoxxService(DBConfig dbConfig) {
        Client http = ClientBuilder.newClient();
        baseTarget = http.target(
                "http://" + dbConfig.getDbHost() + ":" + dbConfig.getDbPort() + "/_db/" + dbConfig.getDbName());
    }

    @Override
    public void onConnected(ArangoDatabase db) {
        InputStream is = getClass().getResourceAsStream("/grapheus-foxx.zip");
        WebTarget target = baseTarget.path("_api/foxx").queryParam("mount", "/" + NAME);
        Response r = target.request().post(Entity.entity(is, MediaType.APPLICATION_OCTET_STREAM_TYPE));

        if(r.getStatus() != 201) {
            String errorResponseBody = r.readEntity(String.class);
            log.error("Error while uploading foxx service to {} (code {}):\n{}", target.getUri(), r.getStatus(), errorResponseBody);
        }
    }
}
