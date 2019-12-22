/**
 *
 */
package org.grapheus.web.component.vicinity.view;

import com.googlecode.wicket.jquery.ui.interaction.droppable.DroppableAdapter;
import com.googlecode.wicket.jquery.ui.interaction.droppable.DroppableBehavior;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.grapheus.client.model.graph.RGraph;
import org.grapheus.web.RemoteUtil;
import org.grapheus.web.ShowOperationSupport;
import org.grapheus.web.component.list.view.VerticesListViewPanel;
import org.grapheus.web.component.operation.dialog.collapsed.GenerateCollapsedGraphPanel;
import org.grapheus.web.model.VicinityGraph;
import org.grapheus.web.model.WEdge;
import org.grapheus.web.model.WVertex;
import org.grapheus.web.state.GlobalFilter;
import org.grapheus.web.state.GlobalStateController;
import org.grapheus.web.state.VicinityVisualizationConfig;
import org.grapheus.web.state.event.GraphViewChangedEvent;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

/**
 * @author black
 *
 */
public class VicinityInteractiveView extends Panel {
    private static final long serialVersionUID = 1L;

    private static final PackageTextTemplate graphActivatorTemplate = new PackageTextTemplate(
            VicinityInteractiveView.class, "js/graphSettings.js");

    // Fields come from the constructor parameters
    private final ShowOperationSupport dialogOperationSupport;
    private final VicinityVisualizationConfig visualizationConfig;
    private final GlobalStateController globalStateController;

    // Fields created internally in the constructor
    private final AbstractDefaultAjaxBehavior navigateBehavior;
    private final AbstractDefaultAjaxBehavior deleteEdgeBehavior;
    private final AbstractDefaultAjaxBehavior deleteVertexBehavior;
    private final AbstractDefaultAjaxBehavior generateCollapsedGraphBehavior;
    private final AbstractDefaultAjaxBehavior vertexExpansionBehavior;

    public VicinityInteractiveView(final String id,
                                   GlobalStateController globalStateController,
                                   VicinityVisualizationConfig visualizationConfig,
                                   ShowOperationSupport dialogOperationSupport) {
        super(id);
        this.visualizationConfig = visualizationConfig;
        this.globalStateController = globalStateController;
        this.dialogOperationSupport = dialogOperationSupport;
        navigateBehavior = createNavigateBehavior();
        deleteEdgeBehavior = createDeleteEdgeBehavior();
        deleteVertexBehavior = createDeleteVertexBehavior();
        generateCollapsedGraphBehavior = createGenerateCollapsedGraphBehavior();
        vertexExpansionBehavior = createVertexExpansionBehavior();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(JavaScriptHeaderItem
                .forReference(getApplication().getJavaScriptLibrarySettings().getJQueryReference()));

        Map<String, CharSequence> jsParams = new HashMap<>();
        jsParams.put("navigateCallbackURL", navigateBehavior.getCallbackUrl());
        jsParams.put("layout", visualizationConfig.getLayout().getJsName());
        jsParams.put("deleteEdgeURL", deleteEdgeBehavior.getCallbackUrl());
        jsParams.put("deleteVertexURL", deleteVertexBehavior.getCallbackUrl());
        jsParams.put("generateCollapsedGraphURL", generateCollapsedGraphBehavior.getCallbackUrl());
        jsParams.put("sourceGraphURL", isGenerativePropertySet() ? vertexExpansionBehavior.getCallbackUrl() : "");
        response.render(OnLoadHeaderItem.forScript(graphActivatorTemplate.asString(jsParams)));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        IModel<VicinityGraph> vicinityVertexModel = globalStateController.getSharedModels().getVicinityGraphModel();

        add(new WebComponent("rootVertex")
                .add(new AttributeAppender("vertexId",
                        new PropertyModel<String>(globalStateController.getFilter(), GlobalFilter.SELECTED_VERTEX_ID))));

        add(createVerticesList("linkedArtifactsList", vicinityVertexModel));

        add(createEdgesList("edgesList", vicinityVertexModel));

        add(navigateBehavior);
        add(deleteEdgeBehavior);
        add(deleteVertexBehavior);
        add(generateCollapsedGraphBehavior);
        add(vertexExpansionBehavior);
        add(newDroppabe(".vicinityView"));
    }


    public String getGenerativeGraphProperty() {
        return ofNullable(getCurrentGraph()).map(RGraph::getGenerativeProperty).orElse(null);
    }

    private RGraph getCurrentGraph() {
        return globalStateController.getSharedModels().getCurrentGraphsModel().getObject();
    }

    public String getGenerativeGraphId() {
        return ofNullable(getCurrentGraph()).map(RGraph::getGenerativeGraphId).orElse(null);
    }

    public boolean isGenerativePropertySet() {
        return ofNullable(getCurrentGraph())
                .map(RGraph::getGenerativeProperty)
                .isPresent();
    }

    private Behavior newDroppabe(String selector) {
        return new DroppableBehavior(selector, new DroppableAdapter() {

            private static final long serialVersionUID = 1L;

            @Override
            public void onDrop(AjaxRequestTarget target, Component component) {
                HttpSession session = ((ServletWebRequest) RequestCycle.get()
                        .getRequest()).getContainerRequest().getSession();

                VerticesListViewPanel.VertexInfo data = (VerticesListViewPanel.VertexInfo) session.getAttribute("draggingVertex");

                GlobalFilter filter = globalStateController.getFilter();
                RemoteUtil.operationAPI().connect(filter.getGraphId(),
                        Collections.singletonList(data.getVertexId()),
                        Collections.singletonList(filter.getSelectedVertexId()));
                target.add(VicinityInteractiveView.this);
            }
        });
    }

    private AbstractDefaultAjaxBehavior createGenerateCollapsedGraphBehavior() {
        return new AbstractDefaultAjaxBehavior() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void respond(final AjaxRequestTarget target) {
                String generativeProperty = getRequest().getRequestParameters().getParameterValue("generativeProperty").toOptionalString();
                dialogOperationSupport.showOperation(target, new GenerateCollapsedGraphPanel(dialogOperationSupport.getId(), filter().getGraphId(), generativeProperty));
            }
        };
    }

    private AbstractDefaultAjaxBehavior createVertexExpansionBehavior() {
        return new AbstractDefaultAjaxBehavior() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void respond(final AjaxRequestTarget target) {
                String propertyValue = getRequest().getRequestParameters().getParameterValue("generativeValue").toOptionalString();
                String propertyName = getGenerativeGraphProperty();
                String sourceGraphId = getGenerativeGraphId();

                GlobalFilter filter = globalStateController.getFilter();
                filter.setGraphId(sourceGraphId);
                filter.setSelectedPropertyName(propertyName);
                filter.setSelectedPropertyValue(propertyValue);
                filter.setListPropertyFilterMode(GlobalFilter.PropertyFilterMode.PREFIX);
                send(VicinityInteractiveView.this, Broadcast.BUBBLE, new GraphViewChangedEvent(target));
            }
        };
    }

    private AbstractDefaultAjaxBehavior createNavigateBehavior() {
        return new AbstractDefaultAjaxBehavior() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void respond(final AjaxRequestTarget target) {
                String vertexId = getRequest().getRequestParameters().getParameterValue("targetVertextId").toOptionalString();

                filter().setSelectedVertexId(vertexId);
                send(VicinityInteractiveView.this, Broadcast.BUBBLE, new GraphViewChangedEvent(target));
            }
        };
    }

    private AbstractDefaultAjaxBehavior createDeleteEdgeBehavior() {
        return new AbstractDefaultAjaxBehavior() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void respond(final AjaxRequestTarget target) {
                String sourceVertexId = getRequest().getRequestParameters().getParameterValue("sourceId").toOptionalString();
                String targetVertexId = getRequest().getRequestParameters().getParameterValue("targetId").toOptionalString();
                RemoteUtil.operationAPI().disconnect(filter().getGraphId(), sourceVertexId, targetVertexId);
                target.add(VicinityInteractiveView.this);
            }
        };
    }

    private AbstractDefaultAjaxBehavior createDeleteVertexBehavior() {
        return new AbstractDefaultAjaxBehavior() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void respond(final AjaxRequestTarget target) {
                String vertexId = getRequest().getRequestParameters().getParameterValue("vertexId").toOptionalString();
                RemoteUtil.vertexAPI().delete(filter().getGraphId(), vertexId);
                send(VicinityInteractiveView.this, Broadcast.BUBBLE, new GraphViewChangedEvent(target));
            }
        };
    }

    private Component createEdgesList(String id, IModel<VicinityGraph> vicinityVerticesModel) {
        return new ListView<WEdge>(id, new PropertyModel<>(vicinityVerticesModel, VicinityGraph.FIELD_EDGES)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<WEdge> item) {
                WEdge edge = item.getModelObject();

                WebComponent l = new WebComponent("edge");
                l.add(new AttributeAppender("from", edge.getFromId()));
                l.add(new AttributeAppender("to", edge.getToId()));
                String serializedTags = Optional.ofNullable(edge.getTags())
                        .map(tags -> String.join(",", tags))
                        .orElse("");
                l.add(new AttributeAppender("tags", serializedTags));

                item.add(l);
            }
        };
    }

    private Component createVerticesList(String id, IModel<VicinityGraph> vertexVicinityModel) {
        return new ListView<WVertex>(id, new PropertyModel<>(vertexVicinityModel, VicinityGraph.FIELD_VERTICES)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<WVertex> item) {
                WVertex vertex = item.getModelObject();
                WebComponent l = new WebComponent("vertex");
                l.add(new AttributeAppender("vertexId", vertex.getId()));
                l.add(new AttributeAppender("name", vertex.getName()));

                if(containsSelectedProperty(vertex)) {
                    l.add(new AttributeAppender("highlighted", vertex.getName()));
                }

                String serializedTags = Optional.ofNullable(vertex.getTags())
                        .map(tags -> String.join(",", tags))
                        .orElse("");
                l.add(new AttributeAppender("tags", serializedTags));
                l.add(new AttributeAppender("generativeValue", vertex.getGenerativeValue()));

                String serializedProperties = Optional.ofNullable(vertex.getProperties())
                        .map(props -> props.stream()
                                .map(p -> p.getName() + ":" + p.getValue())
                                .collect(Collectors.joining(",")))
                        .orElse("");
                l.add(new AttributeAppender("properties", serializedProperties));

                item.add(l);
            }
        };
    }

    private boolean containsSelectedProperty(WVertex vertex) {
        String selectedPropertyName = filter().getSelectedPropertyName();
        String selectedPropertyValue = filter().getSelectedPropertyValue();
        if(selectedPropertyName == null || selectedPropertyValue == null) {
            return false;
        }
        return vertex.getProperties().stream()
                .anyMatch(p->p.getName().equals(selectedPropertyName) && p.getValue().equals(selectedPropertyValue));
    }

    private GlobalFilter filter() {
        return globalStateController.getFilter();
    }
}
