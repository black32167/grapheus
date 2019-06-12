/**
 * 
 */
package grapheus.persistence.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.grapheus.client.model.graph.SortDirection;
import org.grapheus.client.model.graph.edge.EdgeDirection;

import lombok.NonNull;

/**
 * @author black
 */
public class EdgesFilterQuery {
    private final String startingVertexId;
    private final String subqueryCollectionName;
    private final Map<String, Object> parameters;
    private final List<String> verticesSortCriteria;
    private final EdgeDirection edgesDirection;
    
    private final List<String> edgesClauses = new ArrayList<>();
    private final List<String> vertexClauses;
    
    public EdgesFilterQuery(
            @NonNull String graphName,
            @NonNull String startingVertexId,
            @NonNull String subqueryCollectionName,
            @NonNull Map<String, Object> parameters,
            @NonNull List<String> sortCriteria,
            @NonNull List<String> vertexClauses,
            @NonNull EdgeDirection edgesDirection) {
        this.startingVertexId = startingVertexId;
        this.subqueryCollectionName = subqueryCollectionName;
        this.parameters = parameters;
        this.verticesSortCriteria = sortCriteria;
        this.edgesDirection = edgesDirection;
        this.vertexClauses = vertexClauses;
        parameters.put("graphName", graphName);
    }

    public EdgesFilterQuery sortByEdgesCount(@NonNull SortDirection sortDirection) {
        verticesSortCriteria.add("LENGTH(" + subqueryCollectionName + ") " + sortDirection.name()); // Note: This makes assumptions on how 'vertex' container renders this subquery.
        return this;
    }

    public void addCountFilter(int minCount) {
        vertexClauses.add("LENGTH(" + subqueryCollectionName + ") >= " + minCount);
    }
    
    public String renderAQL() {
        String filter = "";
        if(!edgesClauses.isEmpty()) {
            filter = " FILTER " + edgesClauses.stream().collect(Collectors.joining(" AND "));
        }
        return "FOR v,e IN 1..1 " + edgesDirection.name() + " " +
                startingVertexId + 
                " GRAPH @graphName" + 
                filter + 
                " RETURN e";
    }

    public void addEdgePropertyFilter(String propertyName, String value) {
        if (propertyName != null) {
            String paramName = "e" + edgesClauses.size();
            parameters.put(paramName, "%" + value + "%");
            edgesClauses.add(" e." + propertyName + " == @" + paramName);
        }
    }

}
