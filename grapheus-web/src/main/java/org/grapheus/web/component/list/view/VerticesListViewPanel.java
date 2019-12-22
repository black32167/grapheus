/**
 * 
 */
package org.grapheus.web.component.list.view;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableAdapter;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableBehavior;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.cycle.RequestCycle;
import org.grapheus.client.model.graph.vertex.RVertex;
import org.grapheus.web.RemoteUtil;
import org.grapheus.web.component.shared.vlink.VertexLinkPanel;
import org.grapheus.web.model.VerticesRemoteDataset;
import org.grapheus.web.state.GlobalFilter;
import org.grapheus.web.state.GlobalStateController;
import org.grapheus.web.state.SharedModels;
import org.grapheus.web.state.event.GraphViewChangedEvent;

import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author black
 */
@Slf4j
public class VerticesListViewPanel extends Panel {
    @Data
    @Builder
    public static class VertexInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String title;
        private final long updatedTimestamp;
        private final String vertexId;
        private final RVertex vertex;
        private final String vertexInfo;
        private final boolean editable;
    }
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd-MM-yyyy mm:HH");

    private final GlobalStateController globalStateController;
    
    @Builder
    public VerticesListViewPanel(
            @NonNull String id,
            @NonNull GlobalStateController globalStateController) {
        super(id);

        this.globalStateController = globalStateController;
       
        // Add list component
        SharedModels sharedModels = globalStateController.getSharedModels();
        add(createListForm("verticesForm")
                .add(getVerticesListPanel("artifacts", sharedModels.getVerticesListModel())));
    }

    private Form<?> createListForm(String id) {
        return new Form<>(id);
    }

    private ListView<VertexInfo> getVerticesListPanel(String componentId, IModel<VerticesRemoteDataset> artifactsListModel) {
        
        ListView<VertexInfo> listView = new ListView<VertexInfo>(componentId, new PropertyModel<>(artifactsListModel, VerticesRemoteDataset.FIELD_VERTICES)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<VertexInfo> item) {
                GlobalFilter globalFilter = globalStateController.getFilter();
                String graphName = globalFilter.getGraphId();
                IModel<VertexInfo> taskModel = (IModel<VertexInfo>) item.getDefaultModel();
                VertexInfo vertexInfo = taskModel.getObject();
               // RVertex vertex = vertexInfo.getVertex();
                String title = vertexInfo.getTitle();

//                item.setModel(Model.of(vertexInfo));
                DraggableBehavior draggableBehavior = new DraggableBehavior("#" + item.getMarkupId(), new DraggableAdapter() {

                    @Override
                    public void onDragStart(AjaxRequestTarget target, int top, int left) {
                        HttpSession session = ((ServletWebRequest)RequestCycle.get()
                                .getRequest()).getContainerRequest().getSession();
                        session.setAttribute("draggingVertex", vertexInfo);//TODO: can we do better? See also VerticesListArgumentPanel
                    }
                    
                });
                
                draggableBehavior.setOptions(new Options()
                        .set("containment", "'#main_content'")
                        .set("cursor", "'pointer'")
                        .set("appendTo", "'body'")
                        .set("helper", "'clone'")
                        );
                item.add(draggableBehavior);
                List<String> selectedVertices = globalFilter.getSelectedVerticesIds();
                item.add(new AjaxCheckBox("selected", new Model<>(selectedVertices.contains(vertexInfo.vertexId))) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                        if(Boolean.TRUE.equals(getDefaultModelObject())) {
                            selectedVertices.add(vertexInfo.vertexId);
                        } else {
                            selectedVertices.remove(vertexInfo.vertexId);
                        }
                    }
                    
                });
                item.add(new VertexLinkPanel(
                        "task_title",
                        title,
                        vertexInfo.getVertexId(),
                        graphName,
                        target -> {
                            globalFilter.setSelectedVertexId(vertexInfo.getVertexId());
                            send(VerticesListViewPanel.this, Broadcast.BUBBLE, new GraphViewChangedEvent(target));
                        }));
                
                item.add(new Label("task_info", vertexInfo.getVertexInfo()));
              
                item.add(new Link<Void>("task_delete") {
                    private static final long serialVersionUID = 1L;
                    @Override
                    public void onClick() {
                        RemoteUtil.vertexAPI().delete(graphName, vertexInfo.getVertexId());
                    }
                }.setVisible(vertexInfo.isEditable()));
            }


        };
       // listView.setReuseItems(true);
        return listView;
        
    }

    private String formatDate(long timeMills) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timeMills), ZoneId.of("UTC")).format(DTF);
    }

    
  
}
