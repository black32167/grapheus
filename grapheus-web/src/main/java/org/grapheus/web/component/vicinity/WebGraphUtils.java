/**
 * 
 */
package org.grapheus.web.component.vicinity;

import org.grapheus.client.model.graph.edge.EdgeDirection;
import org.grapheus.client.model.graph.edge.REdge;
import org.grapheus.client.model.graph.vertex.RVertex;
import org.grapheus.web.RemoteUtil;
import org.grapheus.web.model.Vertex;

import java.util.*;

/**
 * @author black
 *
 */
public class WebGraphUtils {

    public static List<Vertex> listNeighbors(
            final String graphName, 
            final String rootArtifactId, 
            final int depth,
            final EdgeDirection edgesDirection) {

        if(graphName == null || rootArtifactId == null) {
            return Collections.emptyList();
        }
        Collection<REdge> neighborhood = RemoteUtil.vertexAPI().getNeighbors(
                graphName,
                rootArtifactId,
                edgesDirection,
                depth).getEdges();
        Set<String> artifactsIds = new HashSet<>();
        Map<String, List<String>> neighborsMap = new HashMap<>();

        neighborsMap.put(rootArtifactId, new ArrayList<>());
        artifactsIds.add(rootArtifactId);

        for(REdge e: neighborhood) {
            String from = e.getFrom();
            String to = e.getTo();

            neighborsMap.computeIfAbsent(from, (key) -> new ArrayList<>()).add(to);

            artifactsIds.add(from);
            artifactsIds.add(to);
        }

        Collection<RVertex> persistedVertices = RemoteUtil.vertexAPI().loadArtifacts(graphName, artifactsIds);

        List<Vertex> returningVertices = new ArrayList<>();

        for(RVertex persistedVertex: persistedVertices) {
            returningVertices.add(Vertex.builder().//
                    name(persistedVertex.getTitle() != null ? persistedVertex.getTitle() : "#" + persistedVertex.getId()).//
                    id(persistedVertex.getId()).//
                    neighbors(neighborsMap.getOrDefault(persistedVertex.getId(), Collections.emptyList())).//
                    build());
            artifactsIds.remove(persistedVertex.getId());
        }

        // Add remaining 'ephemeral' vertices
        for(String missingVertexId:artifactsIds) {
            returningVertices.add(Vertex.builder()//
                    .id(missingVertexId)
                    .name("#"+missingVertexId)
                    .neighbors(neighborsMap.getOrDefault(missingVertexId, Collections.emptyList()))//
                    .build());
        }
       return returningVertices;
    }
}
