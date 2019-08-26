/**
 * 
 */
package grapheus.persistence.storage.graph.transaction;

import java.nio.charset.Charset;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;

import com.arangodb.ArangoDatabase;
import com.arangodb.model.TransactionOptions;

import grapheus.persistence.conpool.DBConnectionPool;

/**
 * @author black
 *
 */
abstract public class ServerSideTransaction {
    @Inject
    private DBConnectionPool dbTemplate;

    /**
     * Applies ascribed order to nodes in topological order.
     */
    protected <T> T transaction(String transactionCodeFile, Class<T> returnType, TransactionOptions options) {
        return dbTemplate.query((ArangoDatabase db) -> {
            return db.transaction(
                getScript(transactionCodeFile),
                returnType,
                options);
        });
        
    }
    
    private String getScript(String scriptName) {
        try {
            return IOUtils.toString(getClass().getResourceAsStream(scriptName), Charset.forName("UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException("", e);
        }
    }


}
