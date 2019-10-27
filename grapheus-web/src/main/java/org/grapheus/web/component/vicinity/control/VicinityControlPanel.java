/**
 *
 */
package org.grapheus.web.component.vicinity.control;

import com.googlecode.wicket.jquery.ui.widget.dialog.InputDialog;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.grapheus.client.model.graph.edge.EdgeDirection;
import org.grapheus.client.model.graph.vertex.RVertex;
import org.grapheus.web.RemoteUtil;
import org.grapheus.web.ShowOperationSupport;
import org.grapheus.web.component.shared.LambdaAjaxLink;
import org.grapheus.web.component.shared.SerializableConsumer;
import org.grapheus.web.component.vicinity.view.VicinityInteractiveView;
import org.grapheus.web.model.VicinityGraph;
import org.grapheus.web.state.RepresentationState;
import org.grapheus.web.state.VicinityState;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * @author black
 *
 */
@Slf4j
public class VicinityControlPanel extends Panel {
    private static final long serialVersionUID = 1L;

    private static final int MAX_NEIGHBORS_HOPS = 100;

    // This block of fields is initialized from constructor parameters
    private final IModel<VicinityGraph> vicinityVertexModel;
    private final IModel<RVertex> selectedVertexModel;
    private final RepresentationState representationState;
    private final SerializableConsumer<IPartialPageRequestHandler> graphChangedCallback;

    // Fields in  this block are initialized internally in the constructor
    private final Label vertexTagsLabel;
    private final InputDialog<String> titleEditDialog;
    private final VicinityInteractiveView graphView;

    @Builder
    public VicinityControlPanel(String id,
                                final RepresentationState representationState,
                                ShowOperationSupport dialogOperationSupport,
                                final SerializableConsumer<IPartialPageRequestHandler> graphChangedCallback) {
        super(id);
        this.vicinityVertexModel = representationState.getVicinityGraphModel();
        this.selectedVertexModel = createArtifactModel();
        this.graphView = new VicinityInteractiveView("vicinityView", representationState, graphChangedCallback, dialogOperationSupport);
        this.graphView.setOutputMarkupId(true);
        this.titleEditDialog = createTitleEditDialog("titleEditDialog");
        this.graphChangedCallback = graphChangedCallback;
        this.representationState = representationState;
        this.vertexTagsLabel = new Label("vertexTags", selectedVertexTagsModel());
    }

    private IModel<RVertex> createArtifactModel() {
        return new LoadableDetachableModel<RVertex>() {
            private static final long serialVersionUID = 1L;
            @Override
            protected RVertex load() {
                String graphId = representationState.getGraphId();
                String clickedVertexId = representationState.getClickedVertexId();
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

    @Override
    protected void onInitialize() {
        super.onInitialize();

        // "generateLink"
        add(titleEditDialog);
        add(createTitleEditableLink("remoteDocumentLink").add(new Label("documentTitle", new PropertyModel<>(selectedVertexModel, "title"))));
        add(createVertexControlForm("controlsForm")
                .add(newDepthSelector("depth"))
                .add(newDirectionSelector("edgesDirection"))
                .add(newLayoutDropdown("layout"))
                .add(newVerticesTagSelector("selectedVerticesTag"))
                .add(newEdgesTagSelector("selectedEdgesTag")));
        add(new Label("documentBody", new PropertyModel<>(selectedVertexModel, "description")));
        add(vertexTagsLabel);

        add(graphView);
    }

    private Component newVerticesTagSelector(String id) {

        return new DropDownChoice<>(id, allVerticesTagsModel(), new ChoiceRenderer<>(null, "toString()"))
                .setOutputMarkupId(true)
                .add(new AjaxFormComponentUpdatingBehavior("change") {
                    private static final long serialVersionUID = 1L;

                    protected void onUpdate(AjaxRequestTarget target) {
                        target.appendJavaScript("updateNodeColors('" + representationState.getVicinityState().getSelectedVerticesTag() + "');");
                    }
                });
    }

    private IModel<List<String>> allVerticesTagsModel() {
        return new LoadableDetachableModel<List<String>>() {
            @Override
            protected List<String> load() {
                return vicinityVertexModel.getObject().getVertices().stream().flatMap(v -> v.getTags().stream()).distinct().collect(toList());
            }
        };
    }

    private IModel<String> selectedVertexTagsModel() {
        return new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                return Optional.ofNullable(selectedVertexModel.getObject()) //
                        .map(RVertex::getTags) //
                        .map(tags -> String.join(",", tags)) //
                        .orElse("-");
            }
        };
    }

    private Component newEdgesTagSelector(String id) {
        return new DropDownChoice<>(id, edgesTagsModel(), new ChoiceRenderer<>(null, "toString()"))
                .setOutputMarkupId(true)
                .add(new AjaxFormComponentUpdatingBehavior("change") {
                    private static final long serialVersionUID = 1L;

                    protected void onUpdate(AjaxRequestTarget target) {
                        target.appendJavaScript("updateEdgeColors('" + representationState.getVicinityState().getSelectedEdgesTag() + "');");
                    }
                });
    }

    private IModel<List<String>> edgesTagsModel() {
        return new LoadableDetachableModel<List<String>>() {
            @Override
            protected List<String> load() {
                return vicinityVertexModel.getObject().getEdges().stream().flatMap(e -> e.getTags().stream()).distinct().collect(toList());
            }
        };
    }

    private Component newDirectionSelector(String id) {
        return new DropDownChoice<>(id, Arrays.asList(EdgeDirection.values())).
                add(updateGraphViewAjaxBehavior("change"));
    }

    private Component newDepthSelector(String id) {
        final NumberTextField<Integer> depthInput = new NumberTextField<Integer>(id);
        depthInput.setMaximum(MAX_NEIGHBORS_HOPS);
        depthInput.setMinimum(1);
        depthInput.add(updateGraphViewAjaxBehavior("change"));
        return depthInput.setRequired(true);
    }

    private Component newLayoutDropdown(String id) {
        return new DropDownChoice<GraphLayout>(id, Arrays.asList(GraphLayout.values())).
                add(updateGraphViewAjaxBehavior("change"));
    }

    private WebMarkupContainer createTitleEditableLink(String id) {
        LambdaAjaxLink titleLink = new LambdaAjaxLink(id, target -> {
            titleEditDialog.open(target);
        });

        return titleLink;
    }


    private InputDialog<String> createTitleEditDialog(String id) {
        return new InputDialog<String>(id, "Edit title", "", new PropertyModel<>(selectedVertexModel, "title")) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                String newTitle = getModelObject();
                RemoteUtil.vertexAPI().updateVertex(
                        representationState.getGraphId(), representationState.getClickedVertexId(), RVertex.builder().title(newTitle).build());
                graphChangedCallback.accept(target);
            }
        };
    }

    private Form<VicinityState> createVertexControlForm(String formId) {
        return new Form<>(
                formId, new CompoundPropertyModel<>(representationState.getVicinityState()));
    }

    private AjaxEventBehavior updateGraphViewAjaxBehavior(String eventType) {
        return new AjaxFormComponentUpdatingBehavior(eventType) {

            private static final long serialVersionUID = 1L;

            protected void onUpdate(AjaxRequestTarget target) {
                graphChangedCallback.accept(target);
            }
        };
    }
}
