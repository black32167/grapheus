/**
 * 
 */
package org.grapheus.client.model.graph;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author black
 *
 */
@RequiredArgsConstructor
public enum VerticesSortCriteriaType {
    OUT_EDGES_COUNT("outboundEdges"),
    IN_EDGES_COUNT("inboundEdges"),
    VERTEX_TITLE("title"),
    TOPOLOGICAL("topological");

    @Getter
    private final String alias;

    public static VerticesSortCriteriaType fromAlias(String alias) {
        for(VerticesSortCriteriaType type:VerticesSortCriteriaType.values()) {
            if(type.alias.equals(alias)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown sorting criteria type:'" + alias + "'");
    }
}
