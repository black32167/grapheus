/**
 * 
 */
package org.grapheus.web.component.vicinity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.grapheus.client.model.graph.edge.EdgeDirection;
import org.grapheus.client.model.graph.edge.REdge;
import org.grapheus.client.model.graph.vertex.RVertex;
import org.grapheus.web.RemoteUtil;
import org.grapheus.web.model.Vertex;

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
        
        Collection<RVertex> artifacts = RemoteUtil.vertexAPI().loadArtifacts(graphName, artifactsIds);
//        Map<String, RVertex> artifactsMap = artifacts.stream().collect(Collectors.toMap(
//                (a)->a.getId(), (a)->a));
        
        List<Vertex> vertices = artifacts.stream()
                .map(a -> Vertex.builder().//
                    name(a.getTitle() != null ? a.getTitle() : "#"+a.getId()).//
                    id(a.getId()).//
                    neighbors(neighborsMap.getOrDefault(a.getId(), Collections.emptyList())).//
                    build())
                .collect(Collectors.toList());
  
       return vertices;
    }
}
