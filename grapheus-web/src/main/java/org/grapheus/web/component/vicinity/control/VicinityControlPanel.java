/**
 *
 */
package org.grapheus.web.component.vicinity.control;

import com.googlecode.wicket.jquery.ui.interaction.droppable.DroppableAdapter;
import com.googlecode.wicket.jquery.ui.interaction.droppable.DroppableBehavior;
import com.googlecode.wicket.jquery.ui.widget.dialog.InputDialog;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.behavior.Behavior;
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
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.cycle.RequestCycle;
import org.grapheus.client.model.graph.edge.EdgeDirection;
import org.grapheus.client.model.graph.vertex.RVertex;
import org.grapheus.web.RemoteUtil;
import org.grapheus.web.ShowOperationSupport;
import org.grapheus.web.component.list.view.VerticesListViewPanel.VertexInfo;
import org.grapheus.web.component.shared.LambdaAjaxLink;
import org.grapheus.web.component.shared.SerializableConsumer;
import org.grapheus.web.component.shared.SerializableSupplier;
import org.grapheus.web.component.vicinity.view.VicinityInteractiveView;
import org.grapheus.web.model.VicinityModel;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Collections;
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
    private final VicinityModel vicinityVertexModel;
    private final IModel<RVertex> selectedVertexModel;
    private final SerializableSupplier<String> graphIdSupplier;
    private final SerializableConsumer<IPartialPageRequestHandler> graphChangedCallback;

    // Fields in  this block are initialized internally in the constructor
    private final Label vertexTagsLabel;
    private final InputDialog<String> titleEditDialog;
    private final VicinityInteractiveView graphView;

    @Builder
    public VicinityControlPanel(String id,
                                final SerializableSupplier<String> graphIdSupplier,
                                final VicinityModel vicinityVertexModel,
                                ShowOperationSupport dialogOperationSupport,
                                final SerializableConsumer<IPartialPageRequestHandler> graphChangedCallback) {
        super(id);
        this.vicinityVertexModel = vicinityVertexModel;
        this.selectedVertexModel = createArtifactModel();
        this.graphView = new VicinityInteractiveView("vicinityView", vicinityVertexModel, graphIdSupplier, graphChangedCallback, dialogOperationSupport);
        this.graphView.setOutputMarkupId(true);
        this.titleEditDialog = createTitleEditDialog("titleEditDialog");
        this.graphChangedCallback = graphChangedCallback;
        this.graphIdSupplier = graphIdSupplier;
        this.vertexTagsLabel = new Label("vertexTags", selectedVertexTagsModel());
    }

    private IModel<RVertex> createArtifactModel() {
        return new LoadableDetachableModel<RVertex>() {
            private static final long serialVersionUID = 1L;

            @Override
            protected RVertex load() {
                if (graphIdSupplier.get() == null || vicinityVertexModel.getFilter().getSelectedVertexId() == null) {
                    return RVertex.builder().title("none").build();
                }
                try {
                    return RemoteUtil.vertexAPI().getVertex(graphIdSupplier.get(), vicinityVertexModel.getFilter().getSelectedVertexId());
                } catch (Exception e) {
                    log.warn("Could not load vertex #{}", vicinityVertexModel.getFilter().getSelectedVertexId());
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

        add(graphView.add(newDroppabe(".vicinityView")));
    }

    private Component newVerticesTagSelector(String id) {

        return new DropDownChoice<>(id, allVerticesTagsModel(), new ChoiceRenderer<>(null, "toString()"))
                .setOutputMarkupId(true)
                .add(new AjaxFormComponentUpdatingBehavior("change") {
                    private static final long serialVersionUID = 1L;

                    protected void onUpdate(AjaxRequestTarget target) {
                        target.appendJavaScript("updateNodeColors('" + vicinityVertexModel.getFilter().getSelectedVerticesTag() + "');");
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
                        target.appendJavaScript("updateEdgeColors('" + vicinityVertexModel.getFilter().getSelectedEdgesTag() + "');");
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

    private Behavior newDroppabe(String selector) {
        return new DroppableBehavior(selector, new DroppableAdapter() {

            private static final long serialVersionUID = 1L;

            @Override
            public void onDrop(AjaxRequestTarget target, Component component) {
                HttpSession session = ((ServletWebRequest) RequestCycle.get()
                        .getRequest()).getContainerRequest().getSession();

                VertexInfo data = (VertexInfo) session.getAttribute("draggingVertex");//item.getModelObject();//TODO: can we do better? See also VerticesList

                RemoteUtil.operationAPI().connect(graphIdSupplier.get(),
                        Collections.singletonList(data.getVertexId()),
                        Collections.singletonList(vicinityVertexModel.getFilter().getSelectedVertexId()));
                target.add(graphView);
            }

        });

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
                        graphIdSupplier.get(), vicinityVertexModel.getFilter().getSelectedVertexId(), RVertex.builder().title(newTitle).build());
                graphChangedCallback.accept(target);
            }
        };
    }

    private Form<VicinityModel.VicinityState> createVertexControlForm(String formId) {
        return new Form<VicinityModel.VicinityState>(
                formId, new CompoundPropertyModel<VicinityModel.VicinityState>(vicinityVertexModel.getFilter()));
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
