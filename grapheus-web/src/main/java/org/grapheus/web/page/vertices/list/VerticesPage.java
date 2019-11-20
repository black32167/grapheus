package org.grapheus.web.page.vertices.list;

import com.googlecode.wicket.jquery.core.resource.StyleSheetPackageHeaderItem;
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
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.grapheus.client.model.graph.search.RVertexPropertyFilter;
import org.grapheus.web.ShowOperationSupport;
import org.grapheus.web.component.list.vcontrol.VerticesControlPanel;
import org.grapheus.web.component.menu.AjaxMenu;
import org.grapheus.web.component.menu.VertexPageMenuFactory;
import org.grapheus.web.component.operation.tabs.merge.MergePanel;
import org.grapheus.web.component.operation.tabs.rooted.RootedSubgraphGenerationPanel;
import org.grapheus.web.component.vicinity.control.VicinityControlPanel;
import org.grapheus.web.page.base.AbstractGrapheusAuthenticatedPage;
import org.grapheus.web.state.RepresentationState;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VerticesPage extends AbstractGrapheusAuthenticatedPage {
    public static final String PARAM_SELECTED_GRAPH = "graph";
    public static final String PARAM_FILTER_PROPERTY = "filterProperty";
    private static final long serialVersionUID = 1L;
    private static final String SIDE_PANEL_ID = "graphView";

    private static final PackageResourceReference layoutJsReference = new PackageResourceReference(
            VerticesPage.class, "jquery.layout-1.3.0.rc30.80.js");
    private static final PackageResourceReference layoutCssReference = new PackageResourceReference(
            VerticesPage.class, "layout-default-latest.css");
    private static final String FIELD_CURRENT_RIGHT_PANEL_TAB = "currentRightPanelTab";

    private final ShowOperationSupport dialogOperationSupport;

    private final RepresentationState representationState;
    
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

        representationState = new RepresentationState();

        dialog = new ModalWindow("operationDilaog")
                .showUnloadConfirmation(false);
        
        dialogOperationSupport = newDialogOperationSupport();
    }

    private RVertexPropertyFilter parseVertexFilterProperty(String vertexPropFilterStr) {
        if(vertexPropFilterStr == null) {
            return null;
        }

        String[] parts = vertexPropFilterStr.split("=");
        if(parts.length != 2) {
            throw new IllegalArgumentException("Illegal vertex property filter:" + vertexPropFilterStr);
        }

        return new RVertexPropertyFilter(parts[0], parts[1]);
    }

    @Override
    protected void onInitialize() {
        // Choosing current graph
        super.onInitialize();

        initRepresentationState();
        
        add(rightPanel = newRightPanel());

        add(verticesPanel = newVerticesPanel());
        
        add(dialog);
    }

    private void initRepresentationState() {
        String graphId = getPageParameters().get(PARAM_SELECTED_GRAPH).toString();
        representationState.setGraphId(graphId);
        representationState.getVertexListFilter().setVertexPropertyFilter(parseVertexFilterProperty(getPageParameters().get(PARAM_FILTER_PROPERTY).toString()));
    }

    private Component newVerticesPanel() {
        return VerticesControlPanel.builder()
                .id("taskList")
                .representationState(representationState)
                .dialogOperationSupport(dialogOperationSupport)
                .vertexSelectionListener((target, artifactId) -> {
                    showVicinity(target);
                })
                .vertexRemovalListener((target, removedIds) -> {
                    showVicinity(target);
                })
                .build()
                .setOutputMarkupId(true);
    }

    private TabbedPanel<ITab> newRightPanel() {
        List<ITab> tabs = new ArrayList<ITab>();
        tabs.add(newTab(
                "Vicinity",
                id -> VicinityControlPanel.builder()
                        .id(id)
                        .representationState(representationState)
                        .graphChangedCallback(VerticesPage.this::reloadContent)
                        .dialogOperationSupport(dialogOperationSupport)
                        .build()));

        IModel<String> graphIdModel = new PropertyModel<>(representationState, RepresentationState.FIELD_GRAPH_ID);
        tabs.add(newTab("Merge...", id -> MergePanel.builder()//
                .id(id)//
                .graphIdModel(graphIdModel)
                .operationFinishedCallback(VerticesPage.this::reloadContent)
                .build()));
        
        tabs.add(newTab("Find path...", id -> RootedSubgraphGenerationPanel.builder()//
                .id(id)//
                .sourceGraphIdModel(graphIdModel)
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
                .graphIdSupplier(representationState::getGraphId)
                .editPermitted(true)//FIXME:get graph metadata
                .build();
        return new AjaxMenu(id, new ListModel<>(menuFactory.getMenuItems()));
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
