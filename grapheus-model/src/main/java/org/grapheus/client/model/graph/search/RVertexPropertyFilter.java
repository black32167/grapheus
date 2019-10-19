package org.grapheus.client.model.graph.search;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@AllArgsConstructor
@Data
public class RVertexPropertyFilter implements Serializable {
    private final String name;
    private final String value;
}
