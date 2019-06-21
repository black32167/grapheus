/**
 * 
 */
package org.grapheus.web.component.shared.vlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.cycle.RequestCycle;
import org.grapheus.web.component.list.view.VerticesListViewPanel.VertexInfo;
import org.grapheus.web.component.shared.LambdaAjaxLink;
import org.grapheus.web.component.shared.SerializableConsumer;

import com.googlecode.wicket.jquery.ui.interaction.droppable.Droppable;

/**
 * @author black
 */
public class VerticesListArgumentPanel extends Panel {
    private static final long serialVersionUID = 1L;
    
    private final Map<String, VertexInfo> vertices = new HashMap<>();
    private Component verticesListComponent;
    private String panelTitle;
    private SerializableConsumer<AjaxRequestTarget> onChangeCallback;
    
    public VerticesListArgumentPanel(String id, String panelTitle) {
        super(id);
        this.panelTitle = panelTitle;
    }

    public VerticesListArgumentPanel setChangeCallback(SerializableConsumer<AjaxRequestTarget> consumer) {
        this.onChangeCallback = consumer;
        return this;
    }
    
    @Override
    protected void onInitialize() {
        super.onInitialize();
        
        setOutputMarkupId(true);
        add(newDroppabe("droppableArea")
                .add(new Label("panelTitle", panelTitle))
                .add(verticesListComponent = newVerticesList("verticesList", vertices).setOutputMarkupId(true))
                );
        
    }
    

    private MarkupContainer newDroppabe(String id) {
        return new Droppable<VertexInfo>(id) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onDrop(AjaxRequestTarget target, Component component) {
                HttpSession session = ((ServletWebRequest)RequestCycle.get()
                        .getRequest()).getContainerRequest().getSession();
                
               // ListItem<VertexInfo> item = (ListItem<VertexInfo>) component;
                VertexInfo data = (VertexInfo) session.getAttribute("draggingVertex");//item.getModelObject();//TODO: can we do better? See also VerticesList
                vertices.put(data.getVertexId(), data);
                target.add(VerticesListArgumentPanel.this);
                if(onChangeCallback != null) {
                    onChangeCallback.accept(target);
                }
            }
        };
    }

    private Component newVerticesList(String id,  Map<String, VertexInfo> verticesMap) {
        IModel<List<VertexInfo>> listViewModel = new IModel<List<VertexInfo>>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void detach() {}

            @Override
            public List<VertexInfo> getObject() {
                return new ArrayList<>(verticesMap.values());
            }

            @Override
            public void setObject(List<VertexInfo> object) {
                throw new UnsupportedOperationException();
            }
            
        };
        
        return new ListView<VertexInfo>(id, listViewModel) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void populateItem(ListItem<VertexInfo> item) {
                VertexInfo data = item.getModelObject();
                item.add(new Label("vertexTitle", data.getTitle()));
                item.add(new LambdaAjaxLink("itemRemoveLink", target->  {
                    verticesMap.remove(data.getVertexId());
                    target.add(VerticesListArgumentPanel.this);
                    if(onChangeCallback != null) {
                        onChangeCallback.accept(target);
                    }
                }));
            }
        }.setOutputMarkupId(true);
    }
    
    public List<String> getVerticesIds() {
        return vertices.values().stream().map(v->v.getVertexId()).collect(Collectors.toList());
    }

    public void clear() {
        vertices.clear();
        
    }
}
