package org.grapheus.web.state;

import lombok.Getter;
import lombok.Setter;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.grapheus.web.RemoteUtil;
import org.grapheus.web.model.GraphInfo;
import org.grapheus.web.model.VerticesRemoteDataset;
import org.grapheus.web.model.VicinityGraph;
import org.grapheus.web.model.loader.AvailableGraphsLoader;
import org.grapheus.web.model.loader.VertexListLoader;
import org.grapheus.web.model.loader.VicinityGraphLoader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RepresentationState implements Serializable {
    public static final String FIELD_SELECTED_VIDS = "selectedVerticesIds";
    public static final String FIELD_GRAPH_ID = "graphId";
    public static final String FIELD_SELECTED_VERTEX_ID = "clickedVertexId";

    @Getter
    private final String graphId;

    @Getter
    private final VertexListFilter vertexListFilter = new VertexListFilter();

    @Getter
    private final VicinityState vicinityState = new VicinityState();

    @Getter
    @Setter
    private String clickedVertexId;

    @Getter
    private final List<String> selectedVerticesIds = new ArrayList<>();

    private final IModel<List<GraphInfo>> availableGraphsModel;
    private final IModel<VerticesRemoteDataset> verticesListModel;
    private final IModel<VicinityGraph> vicinityGraphModel;

    public RepresentationState(String graphId) {
        availableGraphsModel = createAvailableGraphsModel();
        verticesListModel = createVerticesModel();
        vicinityGraphModel = createVicinityGraphModel();

        if(graphId == null || !isExists(graphId)) {
            List<GraphInfo> availableGraphs = getAvailableGraphs();

            if(!availableGraphs.isEmpty()) {
                graphId = availableGraphs.get(0).getGraphName();
            }
        }
        this.graphId = graphId;
    }

    private boolean isExists(String graphId) {
        return RemoteUtil.graphsAPI().graphExists(graphId);
    }

    public List<GraphInfo> getAvailableGraphs() {
        return availableGraphsModel.getObject();
    }

    public IModel<List<GraphInfo>> getAvailableGraphsModel() {
        return availableGraphsModel;
    }

    public VerticesRemoteDataset getVerticesList() {
        return verticesListModel.getObject();
    }

    public IModel<VerticesRemoteDataset> getVerticesListModel() {
        return verticesListModel;
    }

    public VicinityGraph getVicinityGraph() {
        return vicinityGraphModel.getObject();
    }

    public IModel<VicinityGraph> getVicinityGraphModel() {
        return vicinityGraphModel;
    }

    private IModel<VicinityGraph> createVicinityGraphModel() {
        return new LoadableDetachableModel<VicinityGraph>() {
            @Override
            protected VicinityGraph load() {
                return new VicinityGraphLoader(RepresentationState.this).load();
            }
        };
    }

    private IModel<VerticesRemoteDataset> createVerticesModel() {
        return new LoadableDetachableModel<VerticesRemoteDataset>() {
            @Override
            protected VerticesRemoteDataset load() {
                return new VertexListLoader(RepresentationState.this).load(getVicinityGraph());
            }
        };
    }

    private IModel<List<GraphInfo>> createAvailableGraphsModel() {
        return new LoadableDetachableModel<List<GraphInfo>>() {
            @Override
            protected List<GraphInfo> load() {
                return new AvailableGraphsLoader().loadAvailableGraphs();
            }
        };
    }
}
