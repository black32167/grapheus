package org.grapheus.web.page.vertices.list;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.grapheus.web.RemoteUtil;
import org.grapheus.web.ShowOperationSupport;
import org.grapheus.web.component.list.vcontrol.VerticesControlPanel;
import org.grapheus.web.component.menu.AjaxMenu;
import org.grapheus.web.component.menu.VertexPageMenuFactory;
import org.grapheus.web.component.operation.tabs.merge.MergePanel;
import org.grapheus.web.component.operation.tabs.rooted.RootedSubgraphGenerationPanel;
import org.grapheus.web.component.shared.SerializableSupplier;
import org.grapheus.web.component.vicinity.control.VicinityControlPanel;
import org.grapheus.web.model.GraphInfo;
import org.grapheus.web.model.VerticesListModel;
import org.grapheus.web.model.VicinityModel;
import org.grapheus.web.page.base.AbstractGrapheusAuthenticatedPage;

import com.googlecode.wicket.jquery.core.resource.StyleSheetPackageHeaderItem;

public class VerticesPage extends AbstractGrapheusAuthenticatedPage {
    private static final long serialVersionUID = 1L;
    public static final String PARAM_SELECTED_GRAPH = "graph";
    private static final String SIDE_PANEL_ID = "graphView";

    private static final PackageResourceReference layoutJsReference = new PackageResourceReference(
            VerticesPage.class, "jquery.layout-1.3.0.rc30.80.js");
    private static final PackageResourceReference layoutCssReference = new PackageResourceReference(
            VerticesPage.class, "layout-default-latest.css");
    private static final String FIELD_CURRENT_RIGHT_PANEL_TAB = "currentRightPanelTab";
    
    
    private String graphId;
    
    private final VerticesListModel verticesListModel;
    private final ShowOperationSupport dialogOperationSupport;
    private final VicinityModel vicinityVertexModel;
    
    private Component rightPanel;
    private Component verticesPanel;
    
    @SuppressWarnings("unused")
    private int currentRightPanelTab;
    
    private final ModalWindow dialog;
    
    @FunctionalInterface
    private interface TabPanelSupplier extends Serializable {
        WebMarkupContainer getPanel(String panelId);
    }

    public VerticesPage(final PageParameters parameters) {
        super(parameters);
        graphId = getPageParameters().get(PARAM_SELECTED_GRAPH).toString();
        
        this.vicinityVertexModel = new VicinityModel(()->graphId);
        this.verticesListModel = new VerticesListModel(()->graphId, vicinityVertexModel);
        
        this.dialog = new ModalWindow("operationDilaog")
                .showUnloadConfirmation(false);
        
        dialogOperationSupport = newDialogOperationSupport();
    }

    @Override
    protected void onInitialize() {
        IModel<List<GraphInfo>> graphListModel = loadAvailableGraphs();
        // Choosing current graph
        List<GraphInfo> availableGraphs = graphListModel.getObject();
        if(graphId == null) {
            if(!availableGraphs.isEmpty()) {
                graphId = availableGraphs.get(0).getGraphName();
            }
        }
        if(!isExists(graphId)) {
            // Graph does not exist -> redirecting to page w/o parameters
            setResponsePage(VerticesPage.class);
        }

        super.onInitialize();
        
        
        add(rightPanel = newRightPanel());

        add(verticesPanel = newVerticesPanel(graphListModel));
        
        add(dialog);
        
    }

    private boolean isExists(String graphId) {
        return RemoteUtil.graphsAPI().graphExists(graphId);
    }

    private Component newVerticesPanel(IModel<List<GraphInfo>> graphListModel) {
        return VerticesControlPanel.builder()
                .id("taskList")
                .graphIdSupplier(()->graphId)
                .verticesListModel(verticesListModel)
                .graphListModel(graphListModel)
                .dialogOperationSupport(dialogOperationSupport)
                .vertexSelectionListener((target, artifactId) -> {
                    vicinityVertexModel.getFilter().setSelectedVertexId(artifactId);
                    showVicinity(target);
                })
                .build()
                .setOutputMarkupId(true);
    }

    private TabbedPanel<ITab> newRightPanel() {
        List<ITab> tabs = new ArrayList<ITab>();
        SerializableSupplier<String> graphIdSupplier = ()->graphId;
        tabs.add(newTab(
                "Vicinity", 
                id -> VicinityControlPanel.builder()
                    .id(id)
                    .graphIdSupplier(graphIdSupplier) 
                    .vicinityVertexModel(vicinityVertexModel)
                    .graphChangedCallback(VerticesPage.this::reloadContent)
                    .build()));
//        
//        tabs.add(newTab("Connect...", id -> VerticesConnectPanel.builder()//
//                .id(id)//
//                .graphId(graphId)
//                .operationFinishedCallback(VerticesPage.this::reloadContent)
//                .build()));
//        
//        tabs.add(newTab("Disconnect...", id -> VerticesDisconnectPanel.builder()//
//                .id(id)//
//                .graphId(graphId)
//                .operationFinishedCallback(VerticesPage.this::reloadContent)
//                .build()));
        
        tabs.add(newTab("Merge...", id -> MergePanel.builder()//
                .id(id)//
                .graphId(graphId)
                .operationFinishedCallback(VerticesPage.this::reloadContent)
                .build()));
        
        tabs.add(newTab("Find path...", id -> RootedSubgraphGenerationPanel.builder()//
                .id(id)//
                .sourceGraphId(graphId)
                .operationFinishedCallback(VerticesPage.this::reloadContent)
                .build()));
      
        
        return new AjaxTabbedPanel<ITab>(SIDE_PANEL_ID, tabs, new PropertyModel<Integer>(this, FIELD_CURRENT_RIGHT_PANEL_TAB));
    }

    private ITab newTab(String tabTitle, TabPanelSupplier panelSupplier) {
        return new AbstractTab(Model.of(tabTitle)) {
            private static final long serialVersionUID = 1L;
            private WebMarkupContainer cachedPanel;

            @Override
            public WebMarkupContainer getPanel(String panelId) {
                if(cachedPanel == null) {
                    cachedPanel = panelSupplier.getPanel(panelId);
                }
                return cachedPanel;
            }
        };
    }

    private void showVicinity(IPartialPageRequestHandler target) {
        currentRightPanelTab = 0;
        target.add(rightPanel);
    }

    private void reloadContent(IPartialPageRequestHandler target) {
        showVicinity(target);
        target.add(verticesPanel);
    }
    
    @Override
    protected Component newMenu(String id) {
        VertexPageMenuFactory menuFactory = VertexPageMenuFactory.builder()
                .page(this)
                .dialogOperationSupport(dialogOperationSupport)
                .graphIdSupplier(()->graphId)
                .editPermitted(true)//FIXME:get graph metadata
                .build();
        return new AjaxMenu(id, new ListModel<>(menuFactory.getMenuItems()));
    }
   

    private IModel<List<GraphInfo>> loadAvailableGraphs() {
        return new LoadableDetachableModel<List<GraphInfo>>() {
            private static final long serialVersionUID = 1L;

            @Override
            protected List<GraphInfo> load() {
                return RemoteUtil.graphsAPI().getAvailableGraphs().stream().map(
                        rg -> GraphInfo.builder().graphName(rg.getName()).editPermitted(rg.isEditPermitted()).build())
                        .collect(Collectors.toList());
            }
        };
    }

    private ShowOperationSupport newDialogOperationSupport() {
        return new ShowOperationSupport() {
            private static final long serialVersionUID = 1L;

            @Override
            public void showOperation(AjaxRequestTarget target, Component operationComponent) {
                dialog.setTitle("Operation");//FIXME:differentiate title 
                dialog.setContent(operationComponent);
                dialog.show(target);
            }
            
            @Override
            public String getId() {
                return dialog.getContentId();
            }
            
            @Override
            public void finishOperation(AjaxRequestTarget target) {
                dialog.close(target);
            }
        };
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptHeaderItem
                .forReference(getApplication().getJavaScriptLibrarySettings().getJQueryReference()));
        response.render(JavaScriptReferenceHeaderItem.forReference(layoutJsReference));
        response.render(StyleSheetPackageHeaderItem.forReference(layoutCssReference));
    }
 
}
