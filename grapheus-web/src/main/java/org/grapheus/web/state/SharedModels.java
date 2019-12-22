package org.grapheus.web.state;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.grapheus.client.model.graph.RGraph;
import org.grapheus.client.model.graph.vertex.RVertex;
import org.grapheus.web.RemoteUtil;
import org.grapheus.web.component.vicinity.WebGraphUtils;
import org.grapheus.web.model.GraphView;
import org.grapheus.web.model.VerticesRemoteDataset;
import org.grapheus.web.model.VicinityGraph;
import org.grapheus.web.model.loader.GraphViewFactory;
import org.grapheus.web.model.loader.VertexListLoader;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
public class SharedModels implements Serializable {

    public static final String FIELD_GRAPH_ID = "graphId";

    @Getter
    private final IModel<List<RGraph>> availableGraphsModel;

    @Getter
    private final IModel<RGraph> currentGraphsModel;

    @Getter
    private final IModel<List<GraphView>> availableGraphsViewModel;

    @Getter
    private final IModel<VerticesRemoteDataset> verticesListModel;

    @Getter
    private final IModel<VicinityGraph> vicinityGraphModel;

    @Getter
    private final IModel<RVertex> selectedVertexModel;

    private final List<IModel> models;

    private final GlobalFilter filter;

    public SharedModels(GlobalFilter filter) {
        this.filter = filter;
        availableGraphsModel = createAvailableGraphsModel();
        availableGraphsViewModel = createAvailableGraphsViewsModel();
        verticesListModel = createVerticesModel();
        vicinityGraphModel = createVicinityGraphModel();
        currentGraphsModel = createCurrentGraphModel();
        selectedVertexModel = createSelectedVertexModel();

        models = Arrays.asList(
                availableGraphsModel, availableGraphsViewModel, verticesListModel,
                vicinityGraphModel, currentGraphsModel, selectedVertexModel);
       // vertexFilter = new GlobalFilter(selectedVertexModel);
    }

    public RVertex getActiveVertex() {
        return selectedVertexModel.getObject();
    }
    public IModel<RVertex> getActiveVertexModel() {
        return selectedVertexModel;
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

    public VerticesRemoteDataset getVerticesList() {
        return verticesListModel.getObject();
    }

    private IModel<RVertex> createSelectedVertexModel() {
        return new LoadableDetachableModel<RVertex>() {
            private static final long serialVersionUID = 1L;
            @Override
            protected RVertex load() {
                String graphId = getGraphId();
                String clickedVertexId = filter.getSelectedVertexId();
                if (graphId == null || clickedVertexId == null) {
                    return RVertex.builder().title("none").build();
                }
                try {
                    return RemoteUtil.vertexAPI().getVertex(graphId, clickedVertexId);
                } catch (Exception e) {
                    log.warn("Could not load vertex #{}", clickedVertexId);
                    return null;
                }
            }
        };
    }


    private IModel<RGraph> createCurrentGraphModel() {
        return new LoadableDetachableModel<RGraph>() {
            private static final long serialVersionUID = 1L;
            @Override
            protected RGraph load() {
                String graphId = getGraphId();
                if (graphId == null) {
                    return null;
                }
                try {
                    return RemoteUtil.graphsAPI().getGraph(graphId);
                } catch (Exception e) {
                    log.warn("Could not load graph #{}", graphId);
                    return null;
                }
            }
        };
    }

    private IModel<VicinityGraph> createVicinityGraphModel() {
        return new LoadableDetachableModel<VicinityGraph>() {
            @Override
            protected VicinityGraph load() {
                String graphId = getGraphId();
                if(graphId == null) {
                    return VicinityGraph.builder()
                            .vertices(Collections.emptyList())
                            .edges(Collections.emptyList())
                            .build();
                }
                return WebGraphUtils.listNeighbors(
                        graphId,
                        filter.getSelectedVertexId(),
                        filter.getTraversalDepth(),
                        filter.getTraversalDirection());
            }
        };
    }

    private IModel<VerticesRemoteDataset> createVerticesModel() {
        return new LoadableDetachableModel<VerticesRemoteDataset>() {
            @Override
            protected VerticesRemoteDataset load() {
                VicinityGraph vicinityGraph = vicinityGraphModel.getObject();
                return new VertexListLoader(filter).load(vicinityGraph);
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
        return filter.getGraphId();
    }

    public void detach() {
        models.forEach(IDetachable::detach);
    }
}
