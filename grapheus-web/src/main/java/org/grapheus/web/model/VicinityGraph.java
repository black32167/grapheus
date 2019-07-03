package org.grapheus.web.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class VicinityGraph {
    public final static String FIELD_VERTICES = "vertices";
    public final static String FIELD_EDGES = "edges";

    private List<Vertex> vertices;
    private List<Edge> edges;
}
