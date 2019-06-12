package org.grapheus.web.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder 
public final class Edge {
    String fromId;
    String toId;
}