/**
 * 
 */
package org.grapheus.web.component.list.vcontrol;

import java.util.ArrayList;
import java.util.List;

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
import org.grapheus.web.component.shared.SerializableSupplier;
import org.grapheus.web.model.GraphInfo;
import org.grapheus.web.model.VerticesListModel;
import org.grapheus.web.model.VerticesListModel.VerticesRemoteDataset;
import org.grapheus.web.page.vertices.list.VerticesPage;

import lombok.Builder;

/**
 * @author black
 *
 */
public class VerticesControlPanel extends Panel {
	private static final long serialVersionUID = 1L;
	private final static String FIELD_SELECTED_VERTICES_IDS = "selectedVerticesIds";

	private final VerticesListModel vertexListModel;
	private final VertexSelectionListener vertexSelectionListener;
	private final VertexRemovalListener vertexRemovalListener;
	private final IModel<List<GraphInfo>> graphListModel;
	private final ShowOperationSupport dialogOperationSupport;
	private final SerializableSupplier<String> graphIdSupplier;
	
	private final List<String> selectedVerticesIds = new ArrayList<String>();
	
	private VerticesListViewPanel verticesList;

    private Component itemCountLabel;
	
    @Builder
	public VerticesControlPanel(
	        String id,
	        final SerializableSupplier<String> graphIdSupplier,
	        VerticesListModel verticesListModel,
	        IModel<List<GraphInfo>> graphListModel,
	        ShowOperationSupport dialogOperationSupport,
	        VertexSelectionListener vertexSelectionListener,
			VertexRemovalListener vertexRemovalListener) {
		super(id);
		this.graphIdSupplier = graphIdSupplier;
		this.vertexSelectionListener = vertexSelectionListener;
		this.vertexRemovalListener = vertexRemovalListener;
		this.vertexListModel = verticesListModel;
		this.graphListModel = graphListModel;
		this.dialogOperationSupport = dialogOperationSupport;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		
        
		add(createTagAllCheckbox("tagAll"));
		
		add(VerticesFilterPanel.builder()
		        .id("verticesFilter")
		        .graphIdSupplier(graphIdSupplier)
		        .verticesListFilter(vertexListModel.getFilter())
    	        .filterChangedCallback((target) -> {
            	    // Filter applied - should update list and count
                    target.add(verticesList);
                    target.add(itemCountLabel);
            	})
    	        .build());
		
		GraphInfo suggestedGraphInfo = null;
		if(graphIdSupplier.get() != null) {
		    suggestedGraphInfo = graphListModel.getObject().stream().filter(gi->gi.getGraphName().equals(graphIdSupplier.get())).findFirst().orElse(null);
		}
		add(new Form<Void>("graph_selection_form")
		        .add(new LambdaAjaxDropDownChoice<GraphInfo>(
		                "graph", Model.of(suggestedGraphInfo), graphListModel, GraphsInfosRenderer.INSTANCE, (target, model)-> {
    		            setResponsePage(VerticesPage.class, new PageParameters().add(VerticesPage.PARAM_SELECTED_GRAPH, model.getObject().getGraphName()));
    		        })));
        
        add(itemCountLabel = createItemsCountLabel("items_count").setOutputMarkupId(true));

		verticesList = VerticesListViewPanel.builder()
		        .id("verticesList")
		        .graphIdSupplier(graphIdSupplier)
		        .listModel(vertexListModel)
		        .vertexSelectionModel(new PropertyModel<List<String>>(this, FIELD_SELECTED_VERTICES_IDS))
		        .vertexSelectionListener((target, vId)-> {
        		    if(vertexListModel.getFilter().isRestrictByVicinity()) {
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
                dialogOperationSupport.showOperation(target,
                        new AddVertexPanel(dialogOperationSupport.getId(), graphIdSupplier.get()) {
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
                List<String> verticesIdsToDelete = new ArrayList<>(selectedVerticesIds);
                selectedVerticesIds.clear();
                RemoteUtil.operationAPI().deleteVertices(graphIdSupplier.get(), verticesIdsToDelete);
                target.add(verticesList);
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
                vertexListModel.getObject().getVertices().forEach(v->selectedVerticesIds.add(v.getVertexId()));
            } else {
                selectedVerticesIds.clear();;
            }
            target.add(verticesList);
        });
        
        return checkbox;
    }

}
