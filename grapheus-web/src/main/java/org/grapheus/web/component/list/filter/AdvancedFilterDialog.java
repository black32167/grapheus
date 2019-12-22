/**
 * 
 */
package org.grapheus.web.component.list.filter;

import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import lombok.Builder;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.event.Broadcast;
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
import org.grapheus.web.state.GlobalFilter;
import org.grapheus.web.state.GlobalFilter.PropertyFilterMode;
import org.grapheus.web.state.event.GraphViewChangedEvent;

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

    private final GlobalFilter globalFilter;
    private final Form<GlobalFilter> form;

    @Builder
    public AdvancedFilterDialog(
            String id, String title,
            GlobalFilter globalFilter) {
        super(id, title);
        this.globalFilter = globalFilter;
        
        this.form = new Form<>("filter_form", new CompoundPropertyModel<>(globalFilter));

        form.add(new CheckBox("sinks"))
            .add(new NumberTextField<Integer>("minEdges"))
            .add(new DropDownChoice<>("traversalDirection", Arrays.asList(EdgeDirection.INBOUND, EdgeDirection.OUTBOUND)))
            .add(new DropDownChoice<>("sortingDirection", Arrays.asList(SortDirection.values())))
            .add(new DropDownChoice<>("sortingType", loadAvailableSortTypes(), VerticesSortTypesRenderer.INSTANCE))
            .add(new CheckBox("filterListByTraversalDepth"))
            .add(new DropDownChoice<>("listPropertyFilterMode", Arrays.asList(PropertyFilterMode.values())));
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
                String graphId = globalFilter.getGraphId();                                                             ;
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
        send(AdvancedFilterDialog.this, Broadcast.BUBBLE, new GraphViewChangedEvent(handler));
    }

    @Override
    public DialogButton getSubmitButton() {
        return this.findButton(OK);
    }

    @Override
    public Form<GlobalFilter> getForm() {
        return this.form;
    }

    @Override
    protected void onError(AjaxRequestTarget target) {

    }

    @Override
    protected void onSubmit(AjaxRequestTarget target) {
        send(AdvancedFilterDialog.this, Broadcast.BUBBLE, new GraphViewChangedEvent(target));
    }
}
