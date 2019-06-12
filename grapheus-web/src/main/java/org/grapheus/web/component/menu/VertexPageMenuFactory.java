/**
 * 
 */
package org.grapheus.web.component.menu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.grapheus.web.ShowOperationSupport;
import org.grapheus.web.component.operation.DeleteOperation;
import org.grapheus.web.component.operation.DeleteRogueVerticesOperation;
import org.grapheus.web.component.operation.ExportGraphOperation;
import org.grapheus.web.component.operation.GraphOperation;
import org.grapheus.web.component.operation.ToposortOpertion;
import org.grapheus.web.component.operation.dialog.copy.CopyGraphGenerationPanel;
import org.grapheus.web.component.operation.dialog.cycles.CyclesGraphGenerationPanel;
import org.grapheus.web.component.operation.dialog.empty.EmptyGraphGenerationPanel;
import org.grapheus.web.component.operation.dialog.self.SelfGraphGenerationPanel;
import org.grapheus.web.component.shared.SerializableSupplier;
import org.grapheus.web.page.graph.upload.UploadGraphPage;
import org.grapheus.web.page.vertices.list.VerticesPage;

import com.googlecode.wicket.jquery.ui.widget.menu.IMenuItem;
import com.googlecode.wicket.jquery.ui.widget.menu.MenuItem;

import lombok.Builder;
import lombok.NonNull;

/**
 * @author black
 *
 */
@Builder
public class VertexPageMenuFactory implements Serializable {
    private static final long serialVersionUID = 1L;
    @NonNull private final Page page;
    @NonNull private final SerializableSupplier<String> graphIdSupplier;
    @NonNull private final ShowOperationSupport dialogOperationSupport;
    private final boolean editPermitted;
    
    public List<IMenuItem> getMenuItems() {
        List<IMenuItem> allowedOperations = new ArrayList<>();

        allowedOperations.add(new MenuItem("Graph...", getGraphMenuItems()));
        allowedOperations.add(new MenuItem("Explore...", getExploreMenuItems()));
        

        return allowedOperations;
    }
    
    
    private List<IMenuItem> getGraphMenuItems() {
        List<IMenuItem> operationsMenu = new ArrayList<IMenuItem>();
        
        operationsMenu.add(newOperationItem("Topo sort", new ToposortOpertion(getGraphId())));
        operationsMenu.add(newOperationItem("Delete rogue vertices", new DeleteRogueVerticesOperation(getGraphId())));
//        operationsMenu.add(new MenuItem("Upload vertices...") {
//            private static final long serialVersionUID = 1L;
//            @Override
//            public void onClick(AjaxRequestTarget target){
//                RequestCycle.get().setResponsePage(AddArtifactPage.class,
//                        new PageParameters().add(AddArtifactPage.PARAM_GRAPH_NAME, getGraphId()));
//            }
//        });
        operationsMenu.add(new MenuItem("Import graph...") {
            private static final long serialVersionUID = 1L;
            @Override
            public void onClick(AjaxRequestTarget target){
                RequestCycle.get().setResponsePage(UploadGraphPage.class);
            }
        });
        operationsMenu.add(newDialogItem("New empty graph...", 
                new EmptyGraphGenerationPanel(dialogOperationSupport.getId())));
        operationsMenu.add(newOperationItem("Export graph", new ExportGraphOperation(getGraphId(), page)));
        operationsMenu.add(newOperationItem("Delete graph", new DeleteOperation(getGraphId())));
  
        return operationsMenu;
    }

    private List<IMenuItem> getExploreMenuItems() {
        List<IMenuItem> operations = new ArrayList<IMenuItem>();


        operations.add(newDialogItem("Self graph...", 
                new SelfGraphGenerationPanel(dialogOperationSupport.getId())));
        operations.add(newDialogItem("Duplicate", 
                new CopyGraphGenerationPanel(dialogOperationSupport.getId(), getGraphId())));
        operations.add(newDialogItem("Find cycles...", 
                new CyclesGraphGenerationPanel(dialogOperationSupport.getId(), getGraphId())));
     
        return operations;
      
    }


    private IMenuItem newOperationItem(String title, GraphOperation graphOperation) {
        return new MenuItem(title) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                if(!graphOperation.apply(target)) {
                    RequestCycle.get().setResponsePage(VerticesPage.class, 
                            new PageParameters().add(VerticesPage.PARAM_SELECTED_GRAPH, getGraphId()));
                }
            }
        };
    }
    private String getGraphId() {
        return graphIdSupplier.get();
    }
    private IMenuItem newDialogItem(String menuItemTitle, Component dialogContent) {
        return new MenuItem(menuItemTitle) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                dialogOperationSupport.showOperation(target, dialogContent);
            }

        };
    }


}
