package org.grapheus.web.model;

import lombok.Builder;
import lombok.Data;
import org.grapheus.client.model.graph.vertex.RVertex;

import java.io.Serializable;
import java.util.List;

@Data
@Builder 
public final class WVertex implements Serializable {
    private static final long serialVersionUID = 1L;

    String name;
    String id;
    List<String> tags;
    List<RVertex.RProperty> properties;
    String generativeValue;
}