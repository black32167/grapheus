/**
 * 
 */
package org.grapheus.web.component.list.vcontrol;

import lombok.Builder;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.grapheus.web.ShowOperationSupport;
import org.grapheus.web.component.list.filter.VerticesFilterPanel;
import org.grapheus.web.component.list.view.VerticesListViewPanel;
import org.grapheus.web.component.operation.dialog.add.AddVertexPanel;
import org.grapheus.web.component.shared.LambdaAjaxCheckbox;
import org.grapheus.web.component.shared.LambdaAjaxDropDownChoice;
import org.grapheus.web.model.GraphView;
import org.grapheus.web.model.VerticesRemoteDataset;
import org.grapheus.web.page.vertices.list.VerticesPage;
import org.grapheus.web.state.GlobalFilter;
import org.grapheus.web.state.GlobalStateController;
import org.grapheus.web.state.SharedModels;
import org.grapheus.web.state.event.GraphViewChangedEvent;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author black
 *
 */
public class VerticesControlPanel extends Panel {
	private static final long serialVersionUID = 1L;
	private final static String FIELD_SELECTED_VERTICES_IDS = "selectedVerticesIds";

	private final IModel<VerticesRemoteDataset> vertexListModel;
	private final IModel<List<GraphView>> graphListModel;
	private final ShowOperationSupport dialogOperationSupport;
	private final GlobalStateController globalStateController;

	private VerticesListViewPanel verticesList;

    @Builder
	public VerticesControlPanel(
			String id,
			ShowOperationSupport dialogOperationSupport,
			GlobalStateController globalStateController) {
		super(id);
		SharedModels sharedModels = globalStateController.getSharedModels();
		this.vertexListModel = sharedModels.getVerticesListModel();
		this.graphListModel = sharedModels.getAvailableGraphsViewModel();
		this.dialogOperationSupport = dialogOperationSupport;
		this.globalStateController = globalStateController;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		
        String graphId = filter().getGraphId();
		add(createTagAllCheckbox("tagAll"));
		
		add(VerticesFilterPanel.builder()
		        .id("verticesFilter")
				.globalFilter(globalStateController.getFilter())
    	        .build());
		
		GraphView suggestedGraphView = null;
		if(graphId != null) {
		    suggestedGraphView = graphListModel.getObject().stream().filter(gi->gi.getGraphId().equals(graphId)).findFirst().orElse(null);
		}
		add(new Form<Void>("graph_selection_form")
		        .add(new LambdaAjaxDropDownChoice<GraphView>(
		                "graph", Model.of(suggestedGraphView), graphListModel, GraphsInfosRenderer.INSTANCE, (target, model)-> {
    		            setResponsePage(VerticesPage.class, new PageParameters().add(VerticesPage.PARAM_SELECTED_GRAPH, model.getObject().getGraphId()));
    		        })));
        
        add(createItemsCountLabel("items_count").setOutputMarkupId(true));

		verticesList = VerticesListViewPanel.builder()
				.globalStateController(globalStateController)
		        .id("verticesList")
				.build();
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
				String graphId = filter().getGraphId();
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
				globalStateController.removeSelectedVertices();
				send(VerticesControlPanel.this, Broadcast.BUBBLE, new GraphViewChangedEvent(target));
				//target.add(verticesList);
            }
        });
    }

    private Component createItemsCountLabel(String labelId) {
        return new Label(labelId, new PropertyModel<String>(vertexListModel, VerticesRemoteDataset.FIELD_TOTAL_COUNT));
    }

    private Component createTagAllCheckbox(String id) {
        IModel<Boolean> cbModel = Model.of(false);
        LambdaAjaxCheckbox checkbox = new LambdaAjaxCheckbox(id, cbModel, (target) -> {
        	GlobalFilter globalFilter = globalStateController.getFilter();
            if(Boolean.TRUE.equals(cbModel.getObject())) {
				globalFilter.getSelectedVerticesIds().addAll(
						vertexListModel.getObject().getVertices()
								.stream()
								.map(VerticesListViewPanel.VertexInfo::getVertexId)
								.collect(Collectors.toList()));
            } else {
				globalFilter.getSelectedVerticesIds().clear();
            }
            target.add(verticesList);
        });
        
        return checkbox;
    }

    private GlobalFilter filter() {
    	return globalStateController.getFilter();
	}
}
