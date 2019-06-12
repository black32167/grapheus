/**
 * 
 */
package org.grapheus.web.component.operation.dialog.add;

import java.util.Optional;
import java.util.UUID;

import org.apache.wicket.ajax.AjaxPreventSubmitBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.grapheus.client.model.graph.vertex.RVertex;
import org.grapheus.web.RemoteUtil;
import org.grapheus.web.page.vertices.list.VerticesPage;

/**
 * @author black
 *
 */
public class AddVertexPanel extends Panel {
    private static final long serialVersionUID = 1L;
    
    private String documentTitle;
    private String documentBody;
    private final String graphName;
    private Form<AddVertexPanel> form;
    private FeedbackPanel feedback;
    
    public AddVertexPanel(String id, String graphName) {
        super(id);
        this.graphName = graphName;
    }
    

    @Override
    protected void onInitialize() {
        super.onInitialize();
        //TODO: ajax submit (to display errors)
        add((feedback = new FeedbackPanel("feedback")).setOutputMarkupId(true));
        add(newVertexForm("form")
                .add(new TextField<String>("documentTitle").setRequired(true))
                .add(new TextArea<String>("documentBody"))
                .add(new Button("submitButton").add(newAjaxSubmitBehavior()))
                .add(new AjaxPreventSubmitBehavior())
                .setOutputMarkupId(true));
    }

    private AjaxFormSubmitBehavior newAjaxSubmitBehavior() {
        return new AjaxFormSubmitBehavior(form, "click") {

            private static final long serialVersionUID = 1L;
            
            @Override
            protected void onError(AjaxRequestTarget target) {
                //error(Optional.ofNullable(e.getMessage()).orElse("Error while creating artifact"));
                target.add(feedback);
            }

            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                try {
                    RemoteUtil.vertexAPI().addVertex(graphName, RVertex.builder().//
                        localId(UUID.randomUUID().toString().toUpperCase()).//
                        title(documentTitle).//
                        description(documentBody).//
                        build());
                    onOperationPerformed(target);
                    
                } catch (Exception e) {
                    error(Optional.ofNullable(e.getMessage()).orElse("Error while creating artifact"));
                    target.add(feedback);
                }
            }

            
        };
    }

    protected void onOperationPerformed(AjaxRequestTarget target) {
        setResponsePage(VerticesPage.class, new PageParameters().add(VerticesPage.PARAM_SELECTED_GRAPH, graphName));
        
    }
    private Form<AddVertexPanel> newVertexForm(String string) {
        return (form = new StatelessForm<AddVertexPanel>("form", new CompoundPropertyModel<AddVertexPanel>(this)));
    }

    
}
