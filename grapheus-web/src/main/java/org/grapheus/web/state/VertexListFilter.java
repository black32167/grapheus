package org.grapheus.web.state;

import lombok.Getter;
import lombok.Setter;
import org.grapheus.client.model.graph.SortDirection;
import org.grapheus.client.model.graph.VerticesSortCriteriaType;
import org.grapheus.client.model.graph.edge.EdgeDirection;
import org.grapheus.client.model.graph.search.RVertexPropertyFilter;

import java.io.Serializable;

public class VertexListFilter implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String FIELD_SUBSTRING = "substring";
    public static final String FIELD_SINKS = "sinks";

    @Getter
    @Setter
    private boolean sinks;
    @Getter @Setter
    private String substring;
    @Getter @Setter
    private VerticesSortCriteriaType sortingType = VerticesSortCriteriaType.OUT_EDGES_COUNT;
    @Getter @Setter
    private SortDirection sortingDirection = SortDirection.DESC;
    @Getter @Setter
    private int minEdges;
    @Getter @Setter
    private EdgeDirection filteringEdgesDirection = EdgeDirection.INBOUND;
    @Getter @Setter
    private boolean restrictByVicinity;
    @Getter @Setter
    private RVertexPropertyFilter vertexPropertyFilter;
}
