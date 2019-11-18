/**
 * 
 */
package org.grapheus.web.component.list.vcontrol;

import lombok.Builder;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.grapheus.web.RemoteUtil;
import org.grapheus.web.ShowOperationSupport;
import org.grapheus.web.component.list.filter.VerticesFilterPanel;
import org.grapheus.web.component.list.view.VertexRemovalListener;
import org.grapheus.web.component.list.view.VertexSelectionListener;
import org.grapheus.web.component.list.view.VerticesListViewPanel;
import org.grapheus.web.component.operation.dialog.add.AddVertexPanel;
import org.grapheus.web.component.shared.LambdaAjaxCheckbox;
import org.grapheus.web.component.shared.LambdaAjaxDropDownChoice;
import org.grapheus.web.model.GraphInfo;
import org.grapheus.web.model.VerticesRemoteDataset;
import org.grapheus.web.page.vertices.list.VerticesPage;
import org.grapheus.web.state.RepresentationState;

import java.util.ArrayList;
import java.util.List;

/**
 * @author black
 *
 */
public class VerticesControlPanel extends Panel {
	private static final long serialVersionUID = 1L;
	private final static String FIELD_SELECTED_VERTICES_IDS = "selectedVerticesIds";

	private final RepresentationState representationState;

	private final IModel<VerticesRemoteDataset> vertexListModel;
	private final VertexSelectionListener vertexSelectionListener;
	private final VertexRemovalListener vertexRemovalListener;
	private final IModel<List<GraphInfo>> graphListModel;
	private final ShowOperationSupport dialogOperationSupport;

	private VerticesListViewPanel verticesList;

    private Component itemCountLabel;
	
    @Builder
	public VerticesControlPanel(
	        String id,
			RepresentationState representationState,
	        ShowOperationSupport dialogOperationSupport,
	        VertexSelectionListener vertexSelectionListener,
			VertexRemovalListener vertexRemovalListener) {
		super(id);
		this.representationState = representationState;
		this.vertexSelectionListener = vertexSelectionListener;
		this.vertexRemovalListener = vertexRemovalListener;
		this.vertexListModel = representationState.getVerticesListModel();
		this.graphListModel = representationState.getAvailableGraphsModel();
		this.dialogOperationSupport = dialogOperationSupport;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		
        String graphId = representationState.getGraphId();
		add(createTagAllCheckbox("tagAll"));
		
		add(VerticesFilterPanel.builder()
		        .id("verticesFilter")
				.representationState(representationState)
    	        .filterChangedCallback((target) -> {
            	    // Filter applied - should update list and count
                    target.add(verticesList);
                    target.add(itemCountLabel);
            	})
    	        .build());
		
		GraphInfo suggestedGraphInfo = null;
		if(graphId != null) {
		    suggestedGraphInfo = graphListModel.getObject().stream().filter(gi->gi.getGraphId().equals(graphId)).findFirst().orElse(null);
		}
		add(new Form<Void>("graph_selection_form")
		        .add(new LambdaAjaxDropDownChoice<GraphInfo>(
		                "graph", Model.of(suggestedGraphInfo), graphListModel, GraphsInfosRenderer.INSTANCE, (target, model)-> {
    		            setResponsePage(VerticesPage.class, new PageParameters().add(VerticesPage.PARAM_SELECTED_GRAPH, model.getObject().getGraphId()));
    		        })));
        
        add(itemCountLabel = createItemsCountLabel("items_count").setOutputMarkupId(true));

		verticesList = VerticesListViewPanel.builder()
		        .id("verticesList")
				.representationState(representationState)
		        .vertexSelectionListener((target, vId)-> {
        		    if(representationState.getVertexListFilter().isRestrictByVicinity()) {
        		        target.add(verticesList);
        		        target.add(itemCountLabel);
        		    }
        		    vertexSelectionListener.onVertexSelected(target, vId);
        		}).build();
        verticesList.setOutputMarkupId(true);
        add(verticesList);
        
        add(newAddControl("addControl"));
        add(newRemoveControl("removeControl"));
	}

    private Component newAddControl(String id) {
        return new WebMarkupContainer(id).add(new AjaxEventBehavior("onclick") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onEvent(AjaxRequestTarget target) {
				String graphId = representationState.getGraphId();
                dialogOperationSupport.showOperation(target,
                        new AddVertexPanel(dialogOperationSupport.getId(), graphId) {
                            private static final long serialVersionUID = 1L;

                            @Override
                            protected void onOperationPerformed(AjaxRequestTarget target) {
                                dialogOperationSupport.finishOperation(target);
                                target.add(verticesList);
                            }
                });
            }
        });
    }

    private Component newRemoveControl(String id) {
        return new WebMarkupContainer(id).add(new AjaxEventBehavior("onclick") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onEvent(AjaxRequestTarget target) {
				String graphId = representationState.getGraphId();
                List<String> verticesIdsToDelete = new ArrayList<>(representationState.getSelectedVerticesIds());
				representationState.getSelectedVerticesIds().clear();
                RemoteUtil.operationAPI().deleteVertices(graphId, verticesIdsToDelete);
                target.add(verticesList);

				String selectedVertexId = representationState.getClickedVertexId();
				if(selectedVertexId != null && verticesIdsToDelete.contains(selectedVertexId)) {
					representationState.setClickedVertexId(null);
				}
				vertexRemovalListener.onVerticesRemoved(target, verticesIdsToDelete);
            }
        });
    }

    private Component createItemsCountLabel(String labelId) {
        return new Label(labelId, new PropertyModel<String>(vertexListModel, VerticesRemoteDataset.FIELD_TOTAL_COUNT));
    }

    private Component createTagAllCheckbox(String id) {
        IModel<Boolean> cbModel = Model.of(false);
        LambdaAjaxCheckbox checkbox = new LambdaAjaxCheckbox(id, cbModel, (target) -> {
            if(Boolean.TRUE.equals(cbModel.getObject())) {
                vertexListModel.getObject().getVertices().forEach(v->representationState.getSelectedVerticesIds().add(v.getVertexId()));
            } else {
				representationState.getSelectedVerticesIds().clear();
            }
            target.add(verticesList);
        });
        
        return checkbox;
    }
}
