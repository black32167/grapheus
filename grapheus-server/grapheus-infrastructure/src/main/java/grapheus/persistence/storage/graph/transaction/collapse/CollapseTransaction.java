package grapheus.persistence.storage.graph.transaction.collapse;

import grapheus.persistence.storage.graph.transaction.FoxxSupport;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CollapseTransaction extends FoxxSupport {
    public void generateCollapsedGraph(String sourceGraphId, String newGraphId, String groupingProperty) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("sourceGraphId", sourceGraphId);
        parameters.put("newGraphId", newGraphId);
        parameters.put("groupingProperty", groupingProperty);

        invokeFoxx("collapse", parameters, String.class);
    }
}
