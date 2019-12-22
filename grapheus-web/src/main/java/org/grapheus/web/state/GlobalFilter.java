package org.grapheus.web.state;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.grapheus.client.model.graph.SortDirection;
import org.grapheus.client.model.graph.VerticesSortCriteriaType;
import org.grapheus.client.model.graph.edge.EdgeDirection;
import org.grapheus.client.model.graph.search.RVertexPropertyFilter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class GlobalFilter implements Serializable {
    public static final String SELECTED_VERTEX_ID = "selectedVertexId";
    public static final String SELECTED_PROPERTY_NAME = "selectedPropertyName";
    public static final String SELECTED_VERTEX_IDS = "selectedVerticesIds";

    public enum PropertyFilterMode {
        NONE, STRICT, PREFIX
    }

    private static final long serialVersionUID = 1L;
    public static final String FIELD_SUBSTRING = "substring";
    public static final String FIELD_SINKS = "sinks";

    private String graphId;
    private boolean sinks;
    private String substring;
    private VerticesSortCriteriaType sortingType = VerticesSortCriteriaType.OUT_EDGES_COUNT;
    private SortDirection sortingDirection = SortDirection.DESC;
    private int minEdges;

    private PropertyFilterMode listPropertyFilterMode = PropertyFilterMode.NONE;
    private final List<String> selectedVerticesIds = new ArrayList<>();
    private String selectedVertexId;
    private String selectedPropertyName;
    private String selectedPropertyValue;

    private int traversalDepth = 1;
    private EdgeDirection traversalDirection = EdgeDirection.INBOUND;
    private boolean filterListByTraversalDepth;


    public RVertexPropertyFilter getVertexPropertyFilter() {
        if(selectedPropertyName != null && selectedPropertyValue != null) {
            switch (listPropertyFilterMode) {
                case STRICT:
                    return new RVertexPropertyFilter(selectedPropertyName, selectedPropertyValue);
                case PREFIX:
                    return new RVertexPropertyFilter(selectedPropertyName, selectedPropertyValue + "%");
            }
        }
        return null;
    }

}
