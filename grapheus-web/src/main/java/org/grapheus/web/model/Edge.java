package org.grapheus.web.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder 
public final class Edge {
    String fromId;
    String toId;
    List<String> tags;
}