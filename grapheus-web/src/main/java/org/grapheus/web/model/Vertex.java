package org.grapheus.web.model;

import java.io.Serializable;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder 
public final class Vertex implements Serializable {
    private static final long serialVersionUID = 1L;

    String name;
    String id;
    List<String> tags;
    List<String> neighbors;
}