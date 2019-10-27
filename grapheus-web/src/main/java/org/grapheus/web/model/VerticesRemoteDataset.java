package org.grapheus.web.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.grapheus.web.component.list.view.VerticesListViewPanel;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class VerticesRemoteDataset {
    public final static String FIELD_VERTICES = "vertices";
    public final static String FIELD_TOTAL_COUNT = "totalCount";

    private long totalCount;
    private List<VerticesListViewPanel.VertexInfo> vertices;
}
