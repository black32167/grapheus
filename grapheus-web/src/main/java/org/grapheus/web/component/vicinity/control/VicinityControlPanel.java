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
import org.apache.wicket.event.Broadcast;
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
import org.grapheus.web.component.vicinity.view.VicinityInteractiveView;
import org.grapheus.web.state.GlobalStateController;
import org.grapheus.web.state.VicinityVisualizationConfig;
import org.grapheus.web.state.event.GraphViewChangedEvent;

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


    // Fields in  this block are initialized internally in the constructor
    private final Label vertexTagsLabel;
    private final InputDialog<String> titleEditDialog;
    private final VicinityInteractiveView graphView;
    private final GlobalStateController globalStateController;
    private final VicinityVisualizationConfig visualizationConfig;

    @Builder
    public VicinityControlPanel(String id,
                                final GlobalStateController globalStateController,
                                ShowOperationSupport dialogOperationSupport) {
        super(id);
        this.globalStateController = globalStateController;
        this.visualizationConfig = new VicinityVisualizationConfig(globalStateController.getFilter());
        this.graphView = new VicinityInteractiveView("vicinityView", globalStateController, visualizationConfig, dialogOperationSupport);
        this.graphView.setOutputMarkupId(true);
        this.titleEditDialog = createTitleEditDialog("titleEditDialog");
        this.vertexTagsLabel = new Label("vertexTags", selectedVertexTagsModel());
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        IModel<RVertex> selectedVertexModel = globalStateController.getSharedModels().getActiveVertexModel();

        // "generateLink"
        add(titleEditDialog);
        add(createTitleEditableLink("remoteDocumentLink").add(new Label("documentTitle", new PropertyModel<>(selectedVertexModel, "title"))));
        add(createVertexControlForm("controlsForm")
                .add(newDepthInput("traversalDepth"))
                .add(newDirectionSelector("edgesDirection"))
                .add(newLayoutDropdown("layout"))
                .add(newVerticesPropertySelector("highlightedProperty"))
                .add(newHierarchyPropertyDepthInput("propertyHierarchyDepth")));
        add(new Label("documentBody", new PropertyModel<>(selectedVertexModel, "description")));
        add(vertexTagsLabel);

        add(graphView);
    }

    private Component newHierarchyPropertyDepthInput(String id) {
        final NumberTextField<Integer> hierarchyDepthInput = new NumberTextField<>(id);
        hierarchyDepthInput.setMaximum(MAX_NEIGHBORS_HOPS);
        hierarchyDepthInput.setMinimum(1);
        hierarchyDepthInput.add(updateGraphViewAjaxBehavior("change"));
        return hierarchyDepthInput.setRequired(true);
    }

    private Component newVerticesPropertySelector(String id) {
        return new DropDownChoice<>(id, allVerticesPropertiesModel(), new ChoiceRenderer<>(null, "toString()"))
                .setOutputMarkupId(true)
                .add(new AjaxFormComponentUpdatingBehavior("change") {
                    private static final long serialVersionUID = 1L;

                    protected void onUpdate(AjaxRequestTarget target) {
                        String selectedVertexSelectedPropertyName = visualizationConfig.getHighlightedProperty();
                        visualizationConfig.setHighlightedProperty(selectedVertexSelectedPropertyName);
                        target.appendJavaScript("updateNodeColors('" + selectedVertexSelectedPropertyName + "');");
                    }
                });
    }

    private IModel<List<String>> allVerticesPropertiesModel() {
        return new LoadableDetachableModel<List<String>>() {
            @Override
            protected List<String> load() {
                RVertex selectedVertex = globalStateController.getSharedModels().getActiveVertex();
                if (selectedVertex != null) {
                    List<String> propertyNames = selectedVertex
                            .getProperties()
                            .stream()
                            .map(RVertex.RProperty::getName)
                            .distinct()
                            .collect(toList());
                    return propertyNames;
                }
                return null;
            }
        };
    }

    private IModel<String> selectedVertexTagsModel() {
        return new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                return Optional.ofNullable(globalStateController.getSharedModels().getActiveVertex()) //
                        .map(RVertex::getTags) //
                        .map(tags -> String.join(",", tags)) //
                        .orElse("-");
            }
        };
    }

    private Component newDirectionSelector(String id) {
        return new DropDownChoice<>(id, Arrays.asList(EdgeDirection.values())).
                add(updateGraphViewAjaxBehavior("change"));
    }

    private Component newDepthInput(String id) {
        final NumberTextField<Integer> depthInput = new NumberTextField<Integer>(id);
        depthInput.setMaximum(MAX_NEIGHBORS_HOPS);
        depthInput.setMinimum(1);
        depthInput.add(updateGraphViewAjaxBehavior("change"));
        return depthInput.setRequired(true);
    }

    private Component newLayoutDropdown(String id) {
        return new DropDownChoice<>(id, Arrays.asList(GraphLayout.values())).
                add(updateGraphViewAjaxBehavior("change"));
    }

    private WebMarkupContainer createTitleEditableLink(String id) {
        return new LambdaAjaxLink(id, titleEditDialog::open);
    }

    private InputDialog<String> createTitleEditDialog(String id) {
        IModel<RVertex> selectedVertexModel = globalStateController.getSharedModels().getActiveVertexModel();
        return new InputDialog<String>(id, "Edit title", "", new PropertyModel<>(selectedVertexModel, "title")) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                String newTitle = getModelObject();
                RemoteUtil.vertexAPI().updateVertex(
                        globalStateController.getFilter().getGraphId(), visualizationConfig.getSelectedVertexId(), RVertex.builder().title(newTitle).build());
                sendGraphUpdated(target);
            }
        };
    }

    private Form<VicinityVisualizationConfig> createVertexControlForm(String formId) {
        return new Form<>(
                formId, new CompoundPropertyModel<>(visualizationConfig));
    }

    private AjaxEventBehavior updateGraphViewAjaxBehavior(String eventType) {
        return new AjaxFormComponentUpdatingBehavior(eventType) {

            private static final long serialVersionUID = 1L;

            protected void onUpdate(AjaxRequestTarget target) {
                sendGraphUpdated(target);
            }
        };
    }

    private void sendGraphUpdated(IPartialPageRequestHandler target) {
        send(VicinityControlPanel.this, Broadcast.BUBBLE, new GraphViewChangedEvent(target));
    }
}
