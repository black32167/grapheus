/**
 * 
 */
package org.grapheus.web.component.vicinity.view;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.grapheus.web.RemoteUtil;
import org.grapheus.web.component.shared.SerializableConsumer;
import org.grapheus.web.component.shared.SerializableSupplier;
import org.grapheus.web.model.Edge;
import org.grapheus.web.model.Vertex;
import org.grapheus.web.model.VicinityModel;

/**
 * @author black
 *
 */
public class VicinityInteractiveView extends Panel {
    private static final long serialVersionUID = 1L;
    

    private static final PackageTextTemplate graphActivatorTemplate = new PackageTextTemplate(
            VicinityInteractiveView.class, "js/graphSettings.js");

    private final VicinityModel vicinityVertexModel;
    private SerializableConsumer<IPartialPageRequestHandler> graphChangedCallback;
    
    private final AbstractDefaultAjaxBehavior navigateBehavior;
    private final AbstractDefaultAjaxBehavior deleteEdgeBehavior;
    private final AbstractDefaultAjaxBehavior deleteVertexBehavior;

    private final SerializableSupplier<String> graphIdSupplier;

    public VicinityInteractiveView(final String id, 
            final VicinityModel vicinityVertexModel,
            final SerializableSupplier<String> graphIdSupplier,
            final SerializableConsumer<IPartialPageRequestHandler> graphChangedCallback) {
        super(id);
        this.graphIdSupplier = graphIdSupplier;
        navigateBehavior = createNavigateBehavior();
        deleteEdgeBehavior = createDeleteEdgeBehavior();
        deleteVertexBehavior = createDeleteVertexBehavior();
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
        response.render(OnLoadHeaderItem.forScript(graphActivatorTemplate.asString(jsParams)));
    }
    
   
    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new WebComponent("rootVertex")
                .add(new AttributeAppender("vertexId", new PropertyModel<String>(vicinityVertexModel.getFilter(), VicinityModel.Filter.FIELD_SELECTED_VERTEX_ID))));
        
        add(createLinkedArtifactsList("linkedArtifactsList", vicinityVertexModel));

        add(createEdgesList("edgesList", vicinityVertexModel));
        
        add(navigateBehavior);
        add(deleteEdgeBehavior);
        add(deleteVertexBehavior);
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

    private Component createEdgesList(String string, IModel<List<Vertex>> vicinityVerticesModel) {
        return new ListView<Edge>("edgesList", getEdgesModel(vicinityVerticesModel)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<Edge> item) {
                Edge edge = item.getModelObject();

                WebComponent l = new WebComponent("edge");
                l.add(new AttributeAppender("from", edge.getFromId()));
                l.add(new AttributeAppender("to", edge.getToId()));
                item.add(l);
            }
        };
    }

    private Component createLinkedArtifactsList(String id, IModel<List<Vertex>> vertexicinityModel) {
        return new ListView<Vertex>(id, vertexicinityModel) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<Vertex> item) {
                Vertex artifact = item.getModelObject();
                WebComponent l = new WebComponent("vertex");
                l.add(new AttributeAppender("vertexId", artifact.getId()));
                l.add(new AttributeAppender("name", artifact.getName()));
                item.add(l);
              
            }
        };
    }

    private IModel<List<Edge>> getEdgesModel(IModel<List<Vertex>> vicinityVerticesModel) {
        return new LoadableDetachableModel<List<Edge>>() {
            private static final long serialVersionUID = 1L;

            @Override
            protected List<Edge> load() {
                List<Edge> edges = vicinityVerticesModel.getObject().stream()
                        .flatMap(v -> vertexToEdgeList(v))
                        .collect(Collectors.<Edge>toList());
             
                return edges;
            }
        };
    }
    
    private Stream<Edge> vertexToEdgeList(Vertex v) {
        return v.getNeighbors().stream().map(n-> {
            String from = /*inbound ? n : */v.getId();
            String to = /*inbound ? v.getId() :*/ n;
            return Edge.builder().fromId(from).toId(to).build();
        });
    }

}
