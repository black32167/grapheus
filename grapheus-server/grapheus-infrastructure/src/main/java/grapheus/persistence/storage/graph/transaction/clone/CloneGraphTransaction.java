/**
 * 
 */
package grapheus.persistence.storage.graph.transaction.clone;

import org.springframework.stereotype.Service;

import com.arangodb.model.TransactionOptions;

import grapheus.persistence.storage.graph.GraphNameUtils;
import grapheus.persistence.storage.graph.transaction.ServerSideTransaction;

/**
 * @author black
 *
 */
@Service
public class CloneGraphTransaction extends ServerSideTransaction  {

    public void generate(String sourceGraph, String newGraphName) {
        String targetVCollection = GraphNameUtils.verticesCollectionName(newGraphName);
        String targetECollection = GraphNameUtils.edgesCollectionName(newGraphName);
        transaction(
                "CloneTransaction.js",
                Void.class,
                new TransactionOptions()
                    .writeCollections(targetVCollection,targetECollection)
                    .params(new String[] {sourceGraph, newGraphName}));
    }

}
