/**
 * 
 */
package org.grapheus.web.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.grapheus.client.api.VerticesFilter;
import org.grapheus.client.model.graph.SortDirection;
import org.grapheus.client.model.graph.VertexInfoType;
import org.grapheus.client.model.graph.VerticesSortCriteria;
import org.grapheus.client.model.graph.VerticesSortCriteriaType;
import org.grapheus.client.model.graph.edge.EdgeDirection;
import org.grapheus.client.model.graph.edge.RAdjacentEdgesFilter;
import org.grapheus.client.model.graph.search.RVertexPropertyFilter;
import org.grapheus.client.model.graph.vertex.RVertex;
import org.grapheus.client.model.graph.vertex.RVertexInfo;
import org.grapheus.client.model.graph.vertex.RVerticesContainer;
import org.grapheus.web.RemoteUtil;
import org.grapheus.web.component.list.view.VerticesListViewPanel.VertexInfo;
import org.grapheus.web.component.shared.SerializableSupplier;
import org.grapheus.web.model.VerticesListModel.VerticesRemoteDataset;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author black
 */
@RequiredArgsConstructor
@Slf4j
public class VerticesListModel extends LoadableDetachableModel<VerticesRemoteDataset> {
    public static class Filter implements Serializable {
        private static final long serialVersionUID = 1L;
        public static final String FIELD_SUBSTRING = "substring";
        public static final String FIELD_SINKS = "sinks";
        
        @Getter @Setter
        private boolean sinks;
        @Getter @Setter
        private String substring;
        @Getter @Setter
        private VerticesSortCriteriaType sortingType = VerticesSortCriteriaType.OUT_EDGES_COUNT;
        @Getter @Setter
        private SortDirection sortingDirection = SortDirection.DESC;
        @Getter @Setter
        private int minEdges;
        @Getter @Setter
        private EdgeDirection filteringEdgesDirection = EdgeDirection.INBOUND;
        @Getter @Setter
        private boolean restrictByVicinity;
        @Getter @Setter
        private RVertexPropertyFilter vertexPropertyFilter;
    }
    
    @Data
    @AllArgsConstructor
    @RequiredArgsConstructor
    @Builder
    public static class VerticesRemoteDataset {
        public final static String FIELD_VERTICES = "vertices";
        public final static String FIELD_TOTAL_COUNT = "totalCount";
        
        private long totalCount;
        private List<VertexInfo> vertices;
    }

    private static final long serialVersionUID = 1L;
    
    @Getter
    private final Filter filter = new Filter();

    private final SerializableSupplier<String> graphIdSupplier;
    
    private final IModel<VicinityGraph> vicinityVerticesListModel;
    
    private VerticesRemoteDataset getVerticesInfoList() {
        VerticesFilter taskFilter = VerticesFilter.builder().//
                sinks(filter.isSinks()).//
                title(filter.getSubstring()).//
                minAdjacentEdgesFilter(RAdjacentEdgesFilter.builder().
                        amount(filter.getMinEdges()).
                        direction(filter.getFilteringEdgesDirection()).build()).
                vertexPropertyFilter(filter.vertexPropertyFilter).
                build();
        if(filter.isRestrictByVicinity()) {
            Set<String> vIds = vicinityVerticesListModel.getObject().getVertices().stream().map(Vertex::getId).collect(Collectors.toSet());
            taskFilter.setVerticesIds(vIds);
        }
        try {
            RVerticesContainer tasksEnvelope = RemoteUtil.vertexAPI().findVertices(graphIdSupplier.get(), taskFilter,
                    filter.getSortingType() == null ? null : new VerticesSortCriteria(filter.getSortingType(), filter.getSortingDirection()));
            
            Collection<RVertex> vertices = tasksEnvelope.getArtifacts();
            Collection<String> verticesKeys = vertices.stream().map(RVertex::getId).collect(Collectors.toList());
            
            Collection<RVertexInfo> vInfos = loadAdditionalInfo(verticesKeys);
            Map<String, String> vId2Info = vInfos.stream().collect(Collectors.toMap(RVertexInfo::getVertexKey, RVertexInfo::getInfoData));
           
            List<VertexInfo> loadedVertices = vertices.stream().map(v-> VertexInfo.builder().//
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
        return VerticesRemoteDataset.builder()
                .vertices(Collections.emptyList())
                .totalCount(0)
                .build();
    }

    private Collection<RVertexInfo> loadAdditionalInfo(Collection<String> verticesKeys) {
        switch(filter.getSortingType()) {
        case IN_EDGES_COUNT:
            return RemoteUtil.vertexAPI().getVerticesInfo(graphIdSupplier.get(), VertexInfoType.INBOUND_EDGES, verticesKeys);
        case OUT_EDGES_COUNT:
            return RemoteUtil.vertexAPI().getVerticesInfo(graphIdSupplier.get(), VertexInfoType.OUTBOUND_EDGES, verticesKeys);
        default:
           return Collections.emptyList();
        }
    }

    @Override
    protected VerticesRemoteDataset load() {
        return getVerticesInfoList();
    }

}
