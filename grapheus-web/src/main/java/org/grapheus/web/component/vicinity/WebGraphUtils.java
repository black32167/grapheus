/**
 * 
 */
package org.grapheus.web.component.vicinity;

import org.grapheus.client.model.graph.edge.EdgeDirection;
import org.grapheus.client.model.graph.edge.REdge;
import org.grapheus.client.model.graph.vertex.RVertex;
import org.grapheus.web.RemoteUtil;
import org.grapheus.web.model.VicinityGraph;
import org.grapheus.web.model.WEdge;
import org.grapheus.web.model.WVertex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

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
        List<WEdge> edgesViews = neighborhood.stream().map(WebGraphUtils::toEdgeView).collect(toList());
        for(WEdge e: edgesViews) {
            String from = e.getFromId();
            String to = e.getToId();
            neighboringArtifactsIds.add(from);
            neighboringArtifactsIds.add(to);
        }

        // Fetching neighboring vertices
        List<WVertex> verticesViews = new ArrayList<>();

        Collection<RVertex> persistedVertices = RemoteUtil.vertexAPI().loadArtifacts(graphName, neighboringArtifactsIds);
        for(RVertex persistedVertex: persistedVertices) {

            verticesViews.add(WVertex.builder().//
                    name(persistedVertex.getTitle() != null ? persistedVertex.getTitle() : "#" + persistedVertex.getId()).//
                    id(persistedVertex.getId()).//
                   // neighbors(neighborsMap.getOrDefault(persistedVertex.getId(), emptyList())).//
                    tags(persistedVertex.getTags()).
                    properties(persistedVertex.getProperties()).
                    generativeValue(persistedVertex.getGenerativeValue()).
                    build());
            neighboringArtifactsIds.remove(persistedVertex.getId());
        }

        // Add remaining 'ephemeral' vertices
        for(String missingVertexId:neighboringArtifactsIds) {
            verticesViews.add(WVertex.builder()//
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

    private static WEdge toEdgeView(REdge remoteEdge) {
        return WEdge.builder()
                .fromId(remoteEdge.getFrom())
                .toId(remoteEdge.getTo())
                .tags(ofNullable(remoteEdge.getTags()).orElse(emptyList()))
                .build();
    }
}
