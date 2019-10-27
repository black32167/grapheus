/**
 * 
 */
package org.grapheus.web.component.list.view;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableAdapter;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableBehavior;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.head.IHeaderResponse;
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
import org.grapheus.web.state.RepresentationState;

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

    private final RepresentationState representationState;
    private final VertexSelectionListener vertexSelectionListener;
    private final IModel<List<String>> vertexSelectionModel;
    
    @Builder
    public VerticesListViewPanel(
            String id,
            RepresentationState representationState,
            VertexSelectionListener vertexSelectionListener) {
        super(id);

        this.representationState = representationState;
        this.vertexSelectionModel = new PropertyModel<>(representationState, RepresentationState.FIELD_SELECTED_VIDS);
        this.vertexSelectionListener = vertexSelectionListener;
       
        // Add list component
        add(createListForm("verticesForm")
                .add(getVerticesListPanel("artifacts", representationState.getVerticesListModel())));
    }

    private Form<?> createListForm(String id) {
        return new Form<>(id);
    }

    private ListView<VertexInfo> getVerticesListPanel(String componentId, IModel<VerticesRemoteDataset> artifactsListModel) {
        
        ListView<VertexInfo> listView = new ListView<VertexInfo>(componentId, new PropertyModel<List<VertexInfo>>(artifactsListModel, VerticesRemoteDataset.FIELD_VERTICES)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<VertexInfo> item) {
                String graphName = representationState.getGraphId();
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
                item.add(new AjaxCheckBox("selected", new Model<Boolean>(vertexSelectionModel.getObject().contains(vertexInfo.vertexId))) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                        if(Boolean.TRUE.equals(getDefaultModelObject())) {
                            vertexSelectionModel.getObject().add(vertexInfo.vertexId);
                        } else {
                            vertexSelectionModel.getObject().remove(vertexInfo.vertexId);
                        }
                    }
                    
                });
                item.add(new VertexLinkPanel(
                        "task_title",
                        title,
                        vertexInfo.getVertexId(),
                        graphName,
                        target -> {
                            representationState.setClickedVertexId(vertexInfo.getVertexId());
                            vertexSelectionListener.onVertexSelected(target, vertexInfo.getVertexId());
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
    
    

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        
       // response.render(OnDomReadyHeaderItem.forScript("setTimeout(()=> { window.layout.resizeAll();}, 500);"));
        //response.render(OnDomReadyHeaderItem.forScript("setTimeout(()=> { $('.artifacts-list').width($('.artifacts-list').parent().width());}, 500);"));
        //response.render(OnDomReadyHeaderItem.forScript("setTimeout(()=> { $('.artifacts-list').width($('.artifacts-list').parent().width());}, 500);"));
    }

    private String formatDate(long timeMills) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timeMills), ZoneId.of("UTC")).format(DTF);
    }

    
  
}
