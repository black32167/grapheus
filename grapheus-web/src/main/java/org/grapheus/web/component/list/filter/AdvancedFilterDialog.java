/**
 * 
 */
package org.grapheus.web.component.list.filter;

import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import lombok.Builder;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.grapheus.client.model.graph.SortDirection;
import org.grapheus.client.model.graph.VerticesSortCriteriaType;
import org.grapheus.client.model.graph.edge.EdgeDirection;
import org.grapheus.web.RemoteUtil;
import org.grapheus.web.component.shared.SerializableConsumer;
import org.grapheus.web.component.shared.SerializableSupplier;
import org.grapheus.web.state.VertexListFilter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author black
 *
 */
//TODO: replace AbstractFormDialog with AbstractDialog
public class AdvancedFilterDialog extends AbstractFormDialog<Serializable> {

    private static final long serialVersionUID = 1L;

    private final SerializableSupplier<String> graphIdSupplier;
    private final Form<VertexListFilter> form;

    private SerializableConsumer<IPartialPageRequestHandler> filterAppliedCallback;

    @Builder
    public AdvancedFilterDialog(String id, String title, SerializableSupplier<String> graphIdSupplier, VertexListFilter verticesListFilter,
            SerializableConsumer<IPartialPageRequestHandler> filterAppliedCallback) {
        super(id, title);
        this.graphIdSupplier = graphIdSupplier;
        this.filterAppliedCallback = filterAppliedCallback;
        
        this.form = new Form<VertexListFilter>("filter_form", new CompoundPropertyModel<VertexListFilter>(verticesListFilter));

        form.add(new CheckBox("sinks"))
            .add(new NumberTextField<Integer>("minEdges"))
            .add(new DropDownChoice<EdgeDirection>("filteringEdgesDirection", Arrays.asList(EdgeDirection.INBOUND, EdgeDirection.OUTBOUND)))
            .add(new DropDownChoice<SortDirection>("sortingDirection", Arrays.asList(SortDirection.values())))
            .add(new DropDownChoice<VerticesSortCriteriaType>("sortingType", loadAvailableSortTypes(), VerticesSortTypesRenderer.INSTANCE))
            .add(new CheckBox("restrictByVicinity"));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        
        add(form);
    }

    private LoadableDetachableModel<List<VerticesSortCriteriaType>> loadAvailableSortTypes() {
        return new LoadableDetachableModel<List<VerticesSortCriteriaType>>() {
            private static final long serialVersionUID = 1L;

            @Override
            protected List<VerticesSortCriteriaType> load() {
                String graphId = graphIdSupplier.get();
                if (graphId == null) {
                    return Collections.emptyList();
                }
                List<VerticesSortCriteriaType> availableSortingCriteria = RemoteUtil.graphsAPI()
                        .getAvailableSortingCriteria(graphId);
                return availableSortingCriteria;
            }

        };

    }

    @Override
    public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
        filterAppliedCallback.accept(handler);
    }

    @Override
    public DialogButton getSubmitButton() {
        return this.findButton(OK);
    }

    @Override
    public Form<VertexListFilter> getForm() {
        return this.form;
    }

    @Override
    protected void onError(AjaxRequestTarget target) {

    }

    @Override
    protected void onSubmit(AjaxRequestTarget target) {
        filterAppliedCallback.accept(target);
    }
}
