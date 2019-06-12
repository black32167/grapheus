/**
 * 
 */
package grapheus.persistence.storage.graph.transaction.cycle;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.arangodb.model.TransactionOptions;

import grapheus.persistence.storage.graph.transaction.ServerSideTransaction;

/**
 * @author black
 *
 */
@Service
public class CyclesSearchTransaction extends ServerSideTransaction {
    public List<List<String>> cycles(String graphName) {
        List<List<String>> cycles = transaction(
                    "CyclesTransaction.js",
                    List.class,
                    new TransactionOptions().params(graphName));
        return Optional.ofNullable(cycles).orElse(Collections.emptyList());
    }
}
