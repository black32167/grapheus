package org.grapheus.web.state;

import lombok.Getter;
import lombok.Setter;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.grapheus.client.model.graph.RGraph;
import org.grapheus.web.RemoteUtil;
import org.grapheus.web.model.GraphView;
import org.grapheus.web.model.VerticesRemoteDataset;
import org.grapheus.web.model.VicinityGraph;
import org.grapheus.web.model.loader.GraphViewFactory;
import org.grapheus.web.model.loader.VertexListLoader;
import org.grapheus.web.model.loader.VicinityGraphLoader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.util.Optional.ofNullable;

public class RepresentationState implements Serializable {
    public static final String FIELD_SELECTED_VIDS = "selectedVerticesIds";
    public static final String FIELD_GRAPH_ID = "graphId";
    public static final String FIELD_SELECTED_VERTEX_ID = "clickedVertexId";

    @Getter
    private RGraph currentGraph;

    @Getter
    private final VertexListFilter vertexListFilter = new VertexListFilter();

    @Getter
    private final VicinityState vicinityState = new VicinityState();

    @Getter
    @Setter
    private String clickedVertexId;

    @Getter
    private final List<String> selectedVerticesIds = new ArrayList<>();

    private final IModel<List<RGraph>> availableGraphsModel;
    private final IModel<List<GraphView>> availableGraphsViewModel;
    private final IModel<VerticesRemoteDataset> verticesListModel;
    private final IModel<VicinityGraph> vicinityGraphModel;

    public RepresentationState() {
        availableGraphsModel = createAvailableGraphsModel();
        availableGraphsViewModel = createAvailableGraphsViewsModel();
        verticesListModel = createVerticesModel();
        vicinityGraphModel = createVicinityGraphModel();
    }

    public void setGraphId(String graphId) {
        currentGraph = RemoteUtil.graphsAPI().getGraph(graphId);

        if(currentGraph == null) {
            currentGraph = getAvailableGraphs().stream().findFirst().orElse(null);
        }
    }

    public boolean isGenerativePropertySet() {
        return ofNullable(currentGraph)
                .map(RGraph::getGenerativeProperty)
                .isPresent();
    }

    private boolean isExists(String graphId) {
        return RemoteUtil.graphsAPI().graphExists(graphId);
    }

    public List<GraphView> getAvailableGraphsViews() {
        return availableGraphsViewModel.getObject();
    }

    public List<RGraph> getAvailableGraphs() {
        return availableGraphsModel.getObject();
    }

    public IModel<List<GraphView>> getAvailableGraphsViewModel() {
        return availableGraphsViewModel;
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

    private IModel<List<RGraph>> createAvailableGraphsModel() {
        return new LoadableDetachableModel<List<RGraph>>() {
            @Override
            protected List<RGraph> load() {
                return RemoteUtil.graphsAPI().getAvailableGraphs();
            }
        };
    }

    private IModel<List<GraphView>> createAvailableGraphsViewsModel() {
        return new LoadableDetachableModel<List<GraphView>>() {
            @Override
            protected List<GraphView> load() {
                return GraphViewFactory.createViews(availableGraphsModel.getObject());
            }
        };
    }

    public String getGraphId() {
        return ofNullable(currentGraph).map(RGraph::getGraphId).orElse(null);
    }

    public String getGenerativeGraphProperty() {
        return ofNullable(currentGraph).map(RGraph::getGenerativeProperty).orElse(null);
    }

    public String getGenerativeGraphId() {
        return ofNullable(currentGraph).map(RGraph::getGenerativeGraphId).orElse(null);
    }
}
