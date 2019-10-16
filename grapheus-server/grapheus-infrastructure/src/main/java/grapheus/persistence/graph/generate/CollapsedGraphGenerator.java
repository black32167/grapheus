package grapheus.persistence.graph.generate;

import grapheus.persistence.exception.GraphExistsException;
import grapheus.persistence.storage.graph.transaction.collapse.CollapseTransaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * Generates graph joining vertices having equal value of the specified property
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__({ @Inject}))
public class CollapsedGraphGenerator {
    private final CollapseTransaction collapseTransaction;
    private final EmptyGraphGenerator emptyGraphGenerator;

    public void generate(String grapheusUserKey, String sourceGraphId, String newGraphId, String groupingProperty) {
        try {
            emptyGraphGenerator.createGraph(grapheusUserKey, newGraphId);
        } catch (GraphExistsException e) {
            log.info("Graph '{}' already exists", newGraphId);
        }
        collapseTransaction.generateCollapsedGraph(sourceGraphId, newGraphId, groupingProperty);
    }
}
