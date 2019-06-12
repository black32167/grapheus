/**
 * 
 */
package org.grapheus.web.component.menu;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.googlecode.wicket.jquery.ui.widget.menu.IMenuItem;
import com.googlecode.wicket.jquery.ui.widget.menu.Menu;

/**
 * @author black
 *
 */
public class AjaxMenu extends Panel {

    private static final long serialVersionUID = 1L;

    private final IModel<List<IMenuItem>> menuItemsModel;
    public AjaxMenu(String id, IModel<List<IMenuItem>> menuItemsModel) {
        super(id);
        this.menuItemsModel = menuItemsModel;
     }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        
        add(newListView("top-menu",  menuItemsModel.getObject()));
    }
    private Component newListView(String id, List<IMenuItem> items) {
        return new ListView<IMenuItem>(id, items) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<IMenuItem> item) {
                IMenuItem menuItem = item.getModelObject();
                item.add(new Label("header-item", menuItem.getTitle()));
                item.add(new Menu("menu", menuItem.getItems()));
            }
            
        };
    }


}
