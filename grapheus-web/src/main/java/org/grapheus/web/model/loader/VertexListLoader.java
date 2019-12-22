package org.grapheus.web.model.loader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.grapheus.client.api.VerticesFilter;
import org.grapheus.client.model.graph.VertexInfoType;
import org.grapheus.client.model.graph.VerticesSortCriteria;
import org.grapheus.client.model.graph.edge.RAdjacentEdgesFilter;
import org.grapheus.client.model.graph.vertex.RVertex;
import org.grapheus.client.model.graph.vertex.RVertexInfo;
import org.grapheus.client.model.graph.vertex.RVerticesContainer;
import org.grapheus.web.RemoteUtil;
import org.grapheus.web.component.list.view.VerticesListViewPanel;
import org.grapheus.web.model.VerticesRemoteDataset;
import org.grapheus.web.model.VicinityGraph;
import org.grapheus.web.model.WVertex;
import org.grapheus.web.state.GlobalFilter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class VertexListLoader {
    private final GlobalFilter globalFilter;

    public VerticesRemoteDataset load(VicinityGraph vicinityGraph) {
        return getVerticesInfoList(vicinityGraph);
    }

    private VerticesRemoteDataset getVerticesInfoList(VicinityGraph vicinityGraph) {
        String graphId = globalFilter.getGraphId();
        if(graphId != null) {
            GlobalFilter vertexListFilter = globalFilter;
            VerticesFilter verticesFilter = VerticesFilter.builder().//
                    sinks(vertexListFilter.isSinks()).//
                    title(vertexListFilter.getSubstring()).//
                    minAdjacentEdgesFilter(RAdjacentEdgesFilter.builder().
                    amount(vertexListFilter.getMinEdges()).
                    direction(vertexListFilter.getTraversalDirection()).build()).
                    vertexPropertyFilter(vertexListFilter.getVertexPropertyFilter()).
                    build();
            if (vertexListFilter.isFilterListByTraversalDepth()) {
                Set<String> vIds = vicinityGraph.getVertices().stream().map(WVertex::getId).collect(Collectors.toSet());
                verticesFilter.setVerticesIds(vIds);
            }
            try {
                RVerticesContainer tasksEnvelope = RemoteUtil.vertexAPI().findVertices(graphId, verticesFilter,
                        vertexListFilter.getSortingType() == null ? null : new VerticesSortCriteria(vertexListFilter.getSortingType(), vertexListFilter.getSortingDirection()));

                Collection<RVertex> vertices = tasksEnvelope.getArtifacts();
                Collection<String> verticesKeys = vertices.stream().map(RVertex::getId).collect(Collectors.toList());

                Collection<RVertexInfo> vInfos = loadAdditionalInfo(verticesKeys);
                Map<String, String> vId2Info = vInfos.stream().collect(Collectors.toMap(RVertexInfo::getVertexKey, RVertexInfo::getInfoData));

                List<VerticesListViewPanel.VertexInfo> loadedVertices = vertices.stream().map(v -> VerticesListViewPanel.VertexInfo.builder().//
                        vertexId(v.getId()).//
                        title(v.getTitle()).//
                        updatedTimestamp(v.getUpdateTimeMills()).//
                        vertexInfo(vId2Info.getOrDefault(v.getId(), "")).//
                        editable(tasksEnvelope.isEditPermitted()).//
                        build()
                ).collect(Collectors.toList());
                return VerticesRemoteDataset.builder()
                        .vertices(loadedVertices)
                        .totalCount(tasksEnvelope.getTotalCount())
                        .build();
            } catch (Exception e) {
                log.error("", e);
            }
        }

        return VerticesRemoteDataset.builder()
                .vertices(Collections.emptyList())
                .totalCount(0)
                .build();
    }

    private Collection<RVertexInfo> loadAdditionalInfo(Collection<String> verticesKeys) {
        String graphId = globalFilter.getGraphId();
        GlobalFilter vertexListFilter = globalFilter;
        switch(vertexListFilter.getSortingType()) {
            case IN_EDGES_COUNT:
                return RemoteUtil.vertexAPI().getVerticesInfo(graphId, VertexInfoType.INBOUND_EDGES, verticesKeys);
            case OUT_EDGES_COUNT:
                return RemoteUtil.vertexAPI().getVerticesInfo(graphId, VertexInfoType.OUTBOUND_EDGES, verticesKeys);
            default:
                return Collections.emptyList();
        }
    }
}
