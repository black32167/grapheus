/**
 * 
 */
package org.grapheus.web.component.vicinity.control;

import java.util.Arrays;
import java.util.Collections;

import javax.servlet.http.HttpSession;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
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
import org.grapheus.web.component.list.view.VerticesListViewPanel.VertexInfo;
import org.grapheus.web.component.shared.LambdaAjaxLink;
import org.grapheus.web.component.shared.SerializableConsumer;
import org.grapheus.web.component.shared.SerializableSupplier;
import org.grapheus.web.component.vicinity.view.VicinityInteractiveView;
import org.grapheus.web.model.VicinityModel;

import com.googlecode.wicket.jquery.ui.interaction.droppable.DroppableAdapter;
import com.googlecode.wicket.jquery.ui.interaction.droppable.DroppableBehavior;
import com.googlecode.wicket.jquery.ui.widget.dialog.InputDialog;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author black
 *
 */
@Slf4j
public class VicinityControlPanel extends Panel {
    private static final long serialVersionUID = 1L;

    private static final int MAX_NEIGHBORS_HOPS = 100;

    private final VicinityInteractiveView graphView;
    
    private final InputDialog<String> titleEditDialog;
    private final IModel<RVertex> artifactModel;
    private final SerializableSupplier<String> graphIdSupplier;
    private final SerializableConsumer<IPartialPageRequestHandler> graphChangedCallback;
    
    private final VicinityModel vicinityVertexModel;

    @Builder
    public VicinityControlPanel(String id, 
            final SerializableSupplier<String> graphIdSupplier,
            final VicinityModel vicinityVertexModel,
            final SerializableConsumer<IPartialPageRequestHandler> graphChangedCallback) {
        super(id);
        this.vicinityVertexModel = vicinityVertexModel;
        this.artifactModel = createArtifactModel();
        this.graphView = new VicinityInteractiveView("vicinityView", vicinityVertexModel, graphIdSupplier, graphChangedCallback);
        this.graphView.setOutputMarkupId(true);
        this.titleEditDialog = createTitleEditDialog("titleEditDialog");
        this.graphChangedCallback = graphChangedCallback;
        this.graphIdSupplier = graphIdSupplier;
    }
    
    private IModel<RVertex> createArtifactModel() {
        return new LoadableDetachableModel<RVertex>() {
            private static final long serialVersionUID = 1L;
            @Override
            protected RVertex load() {
                if(graphIdSupplier.get() == null || vicinityVertexModel.getFilter().getSelectedVertexId() == null) {
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
            add(createTitleEditableLink("remoteDocumentLink").add(new Label("documentTitle", new PropertyModel<>(artifactModel, "title"))));
        add(createVertexControlForm("controlsForm")
                .add(newDepthSelector("depth"))
                .add(newDirectionSelector("edgesDirection"))
                .add(newLayoutDropdown("layout")));
        add(new Label("documentBody", new PropertyModel<>(artifactModel, "description")));
        
        add(graphView.add(newDroppabe(".vicinityView")));
    }

    private Component newDirectionSelector(String id) {
        return new DropDownChoice<EdgeDirection>(id, Arrays.asList(EdgeDirection.values())).
                add(updateGraphViewAjaxBehavior("onchange"));
    }

    private Component newDepthSelector(String id) {
        final NumberTextField<Integer> depthInput = new NumberTextField<Integer>(id);
        depthInput.setMaximum(MAX_NEIGHBORS_HOPS);
        depthInput.setMinimum(1);
        depthInput.add(updateGraphViewAjaxBehavior("onchange"));
        return depthInput.setRequired(true);
    }

    private Behavior newDroppabe(String selector) {
        return new DroppableBehavior(selector, new DroppableAdapter() {

            private static final long serialVersionUID = 1L;

            @Override
            public void onDrop(AjaxRequestTarget target, Component component) {
                HttpSession session = ((ServletWebRequest)RequestCycle.get()
                        .getRequest()).getContainerRequest().getSession();
                
                VertexInfo data = (VertexInfo) session.getAttribute("draggingVertex");//item.getModelObject();//TODO: can we do better? See also VerticesList
                
                RemoteUtil.operationAPI().connect(graphIdSupplier.get(),
                        Collections.singletonList(data.getArtifactId()), 
                        Collections.singletonList(vicinityVertexModel.getFilter().getSelectedVertexId()));
                target.add(graphView);
            }
            
        });

    }
    private Component newLayoutDropdown(String id) {
        return new DropDownChoice<GraphLayout>(id, Arrays.asList(GraphLayout.values())).
                add(updateGraphViewAjaxBehavior("onchange"));
    }

    private WebMarkupContainer createTitleEditableLink(String id) {
        LambdaAjaxLink titleLink = new LambdaAjaxLink(id, target->{
            titleEditDialog.open(target);
        });
     
        return titleLink;
    }


    private InputDialog<String> createTitleEditDialog(String id) {
       return new InputDialog<String>(id, "Edit title", "", new PropertyModel<>(artifactModel, "title")) {
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

    private Form<VicinityModel.Filter> createVertexControlForm(String formId) {
        return new Form<VicinityModel.Filter>(
                formId, new CompoundPropertyModel<VicinityModel.Filter>(vicinityVertexModel.getFilter()));
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
