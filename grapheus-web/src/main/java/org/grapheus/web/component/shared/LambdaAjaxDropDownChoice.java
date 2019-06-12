/**
 * 
 */
package org.grapheus.web.component.shared;

import java.io.Serializable;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

/**
 * @author black
 *
 */
public class LambdaAjaxDropDownChoice<T extends Serializable> extends DropDownChoice<T> {

    private static final long serialVersionUID = 1L;
    private DropdownSerializableConsumer<T> clickCallback;

    public LambdaAjaxDropDownChoice(String id, List<T> items, DropdownSerializableConsumer<T> clickCallback) {
        super(id, items);
        this.clickCallback = clickCallback;
    }
    
    public LambdaAjaxDropDownChoice(String id, List<T> items, IChoiceRenderer<? super T> renderer, DropdownSerializableConsumer<T> clickCallback) {
        super(id, items, renderer);

        this.clickCallback = clickCallback;
    }
    
    public LambdaAjaxDropDownChoice(String id, IModel<List<T>> itemsModel, IChoiceRenderer<? super T> renderer, DropdownSerializableConsumer<T> clickCallback) {
        super(id, itemsModel, renderer);

        this.clickCallback = clickCallback;
    }
    
    public LambdaAjaxDropDownChoice(String id, IModel<List<T>> itemsModel, DropdownSerializableConsumer<T> clickCallback) {
        super(id, itemsModel);

        this.clickCallback = clickCallback;
    }
    

    public LambdaAjaxDropDownChoice(String id, IModel<T> model, IModel<List<T>> itemsModel, IChoiceRenderer<? super T> renderer, DropdownSerializableConsumer<T> clickCallback) {
        super(id, model, itemsModel, renderer);

        this.clickCallback = clickCallback;
    }
    
    public LambdaAjaxDropDownChoice(String id, IModel<T> model, List<T> itemsModel, IChoiceRenderer<? super T> renderer, DropdownSerializableConsumer<T> clickCallback) {
        super(id, model, itemsModel, renderer);

        this.clickCallback = clickCallback;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new OnChangeAjaxBehavior() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(final AjaxRequestTarget target){
                clickCallback.accept(target, getModel());
            }
        });
    }

}
