/**
 *
 */
package org.grapheus.web.component.vicinity.view;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.grapheus.web.RemoteUtil;
import org.grapheus.web.ShowOperationSupport;
import org.grapheus.web.component.operation.collapsed.GenerateCollapsedGraphPanel;
import org.grapheus.web.component.shared.SerializableConsumer;
import org.grapheus.web.component.shared.SerializableSupplier;
import org.grapheus.web.model.Edge;
import org.grapheus.web.model.Vertex;
import org.grapheus.web.model.VicinityGraph;
import org.grapheus.web.model.VicinityModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author black
 *
 */
public class VicinityInteractiveView extends Panel {
    private static final long serialVersionUID = 1L;


    private static final PackageTextTemplate graphActivatorTemplate = new PackageTextTemplate(
            VicinityInteractiveView.class, "js/graphSettings.js");

    // Fields come from the constructor parameters
    private final VicinityModel vicinityVertexModel;
    private final SerializableConsumer<IPartialPageRequestHandler> graphChangedCallback;
    private final ShowOperationSupport dialogOperationSupport;

    // Fields created internally in the constructor
    private final AbstractDefaultAjaxBehavior navigateBehavior;
    private final AbstractDefaultAjaxBehavior deleteEdgeBehavior;
    private final AbstractDefaultAjaxBehavior deleteVertexBehavior;
    private final AbstractDefaultAjaxBehavior generateCollapsedGraphBehavior;

    private final SerializableSupplier<String> graphIdSupplier;

    public VicinityInteractiveView(final String id,
                                   final VicinityModel vicinityVertexModel,
                                   final SerializableSupplier<String> graphIdSupplier,
                                   final SerializableConsumer<IPartialPageRequestHandler> graphChangedCallback,
                                   ShowOperationSupport dialogOperationSupport) {
        super(id);
        this.graphIdSupplier = graphIdSupplier;
        this.dialogOperationSupport = dialogOperationSupport;
        navigateBehavior = createNavigateBehavior();
        deleteEdgeBehavior = createDeleteEdgeBehavior();
        deleteVertexBehavior = createDeleteVertexBehavior();
        generateCollapsedGraphBehavior = createGenerateCollapsedGraphBehavior();
        this.vicinityVertexModel = vicinityVertexModel;
        this.graphChangedCallback = graphChangedCallback;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(JavaScriptHeaderItem
                .forReference(getApplication().getJavaScriptLibrarySettings().getJQueryReference()));

        Map<String, CharSequence> jsParams = new HashMap<>();
        jsParams.put("navigateCallbackURL", navigateBehavior.getCallbackUrl());
        jsParams.put("layout", vicinityVertexModel.getFilter().getLayout().getJsName());
        jsParams.put("deleteEdgeURL", deleteEdgeBehavior.getCallbackUrl());
        jsParams.put("deleteVertexURL", deleteVertexBehavior.getCallbackUrl());
        jsParams.put("generateCollapsedGraphURL", generateCollapsedGraphBehavior.getCallbackUrl());
        response.render(OnLoadHeaderItem.forScript(graphActivatorTemplate.asString(jsParams)));
    }


    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new WebComponent("rootVertex")
                .add(new AttributeAppender("vertexId", new PropertyModel<String>(vicinityVertexModel.getFilter(), VicinityModel.VicinityState.FIELD_SELECTED_VERTEX_ID))));

        add(createVerticesList("linkedArtifactsList", vicinityVertexModel));

        add(createEdgesList("edgesList", vicinityVertexModel));

        add(navigateBehavior);
        add(deleteEdgeBehavior);
        add(deleteVertexBehavior);
        add(generateCollapsedGraphBehavior);
    }

    private AbstractDefaultAjaxBehavior createGenerateCollapsedGraphBehavior() {
        return new AbstractDefaultAjaxBehavior() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void respond(final AjaxRequestTarget target) {
                String vertexId = getRequest().getRequestParameters().getParameterValue("vertexId").toOptionalString();
                dialogOperationSupport.showOperation(target, new GenerateCollapsedGraphPanel(dialogOperationSupport.getId(), graphIdSupplier.get(), vertexId));
            }
        };
    }

    private AbstractDefaultAjaxBehavior createNavigateBehavior() {
        return new AbstractDefaultAjaxBehavior() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void respond(final AjaxRequestTarget target) {
                String vertexId = getRequest().getRequestParameters().getParameterValue("targetVertextId").toOptionalString();
                vicinityVertexModel.getFilter().setSelectedVertexId(vertexId);
                graphChangedCallback.accept(target);
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
                RemoteUtil.operationAPI().disconnect(graphIdSupplier.get(), sourceVertexId, targetVertexId);
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
                RemoteUtil.vertexAPI().delete(graphIdSupplier.get(), vertexId);
                graphChangedCallback.accept(target);
            }

        };
    }

    private Component createEdgesList(String id, IModel<VicinityGraph> vicinityVerticesModel) {
        return new ListView<Edge>(id, new PropertyModel<>(vicinityVerticesModel, VicinityGraph.FIELD_EDGES)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<Edge> item) {
                Edge edge = item.getModelObject();

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
        return new ListView<Vertex>(id, new PropertyModel<>(vertexVicinityModel, VicinityGraph.FIELD_VERTICES)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<Vertex> item) {
                Vertex vertex = item.getModelObject();
                WebComponent l = new WebComponent("vertex");
                l.add(new AttributeAppender("vertexId", vertex.getId()));
                l.add(new AttributeAppender("name", vertex.getName()));

                String serializedTags = Optional.ofNullable(vertex.getTags())
                        .map(tags -> String.join(",", tags))
                        .orElse("");
                l.add(new AttributeAppender("tags", serializedTags));

                String serializedProperties = Optional.ofNullable(vertex.getProperties())
                        .map(props -> props.stream().map(p -> p.getName() + ":" + p.getValue()).collect(Collectors.joining(",")))
                        .orElse("");
                l.add(new AttributeAppender("properties", serializedProperties));

                item.add(l);

            }
        };
    }

}
