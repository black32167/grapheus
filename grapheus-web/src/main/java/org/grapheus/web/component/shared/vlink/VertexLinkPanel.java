/**
 * 
 */
package org.grapheus.web.component.shared.vlink;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.grapheus.web.component.shared.LambdaAjaxLink;
import org.grapheus.web.component.shared.SerializableConsumer;

/**
 * @author black
 *
 */
public class VertexLinkPanel extends Panel {

    private static final long serialVersionUID = 1L;

    public VertexLinkPanel(String id, 
            String title, String artifactId,
            String graphName, 
            SerializableConsumer<AjaxRequestTarget> clickCallback) {
        super(id);
        //add(new Image("details_icon", new PackageResourceReference(VerticesPage.class, "view-details-xxl.png")));
        
        Fragment titleFragment = new Fragment("title", "link_fragment", VertexLinkPanel.this);

        LambdaAjaxLink link = new LambdaAjaxLink("link_url", clickCallback);
//        link.setPopupSettings(new PopupSettings("blank"));
        link.add(new Label("title_label", title));
        titleFragment.add(link);

        add(titleFragment);
       
    }

}
