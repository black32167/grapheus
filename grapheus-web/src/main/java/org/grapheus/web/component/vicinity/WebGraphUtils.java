/**
 * 
 */
package org.grapheus.web.component.vicinity;

import org.grapheus.client.model.graph.edge.EdgeDirection;
import org.grapheus.client.model.graph.edge.REdge;
import org.grapheus.client.model.graph.vertex.RVertex;
import org.grapheus.web.RemoteUtil;
import org.grapheus.web.model.Edge;
import org.grapheus.web.model.Vertex;
import org.grapheus.web.model.VicinityGraph;

import java.util.*;

import static java.util.Collections.*;
import static java.util.Optional.*;
import static java.util.stream.Collectors.*;

/**
 * @author black
 *
 */
public class WebGraphUtils {

    public static VicinityGraph listNeighbors(
            final String graphName, 
            final String rootArtifactId, 
            final int depth,
            final EdgeDirection edgesDirection) {

        if(graphName == null || rootArtifactId == null) {
            return  VicinityGraph.builder()
                    .edges(emptyList())
                    .vertices(emptyList())
                    .build();
        }

        Set<String> neighboringArtifactsIds = new HashSet<>();
        neighboringArtifactsIds.add(rootArtifactId);



        // Fetching neighboring edges
        Collection<REdge> neighborhood = RemoteUtil.vertexAPI().getNeighbors(
                graphName,
                rootArtifactId,
                edgesDirection,
                depth).getEdges();
        List<Edge> edgesViews = neighborhood.stream().map(WebGraphUtils::toEdgeView).collect(toList());
        for(Edge e: edgesViews) {
            String from = e.getFromId();
            String to = e.getToId();
            neighboringArtifactsIds.add(from);
            neighboringArtifactsIds.add(to);
        }

        // Fetching neighboring vertices
        List<Vertex> verticesViews = new ArrayList<>();

        Collection<RVertex> persistedVertices = RemoteUtil.vertexAPI().loadArtifacts(graphName, neighboringArtifactsIds);
        for(RVertex persistedVertex: persistedVertices) {
            verticesViews.add(Vertex.builder().//
                    name(persistedVertex.getTitle() != null ? persistedVertex.getTitle() : "#" + persistedVertex.getId()).//
                    id(persistedVertex.getId()).//
                   // neighbors(neighborsMap.getOrDefault(persistedVertex.getId(), emptyList())).//
                    tags(persistedVertex.getTags()).
                    build());
            neighboringArtifactsIds.remove(persistedVertex.getId());
        }

        // Add remaining 'ephemeral' vertices
        for(String missingVertexId:neighboringArtifactsIds) {
            verticesViews.add(Vertex.builder()//
                    .id(missingVertexId)
                    .name("#"+missingVertexId)
                    .tags(singletonList("external"))
                    //.neighbors(neighborsMap.getOrDefault(missingVertexId, emptyList()))//
                    .build());
        }

        return VicinityGraph.builder()
                .edges(edgesViews)
                .vertices(verticesViews)
                .build();
    }

    private static Edge toEdgeView(REdge remoteEdge) {
        return Edge.builder()
                .fromId(remoteEdge.getFrom())
                .toId(remoteEdge.getTo())
                .tags(ofNullable(remoteEdge.getTags()).orElse(emptyList()))
                .build();
    }
}
