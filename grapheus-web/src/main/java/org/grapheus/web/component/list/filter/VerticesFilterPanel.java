/**
 * 
 */
package org.grapheus.web.component.list.filter;

import lombok.Builder;
import org.apache.wicket.Component;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.grapheus.web.component.shared.LambdaAjaxLink;
import org.grapheus.web.component.shared.LambdaAjaxTextField;
import org.grapheus.web.component.shared.SerializableConsumer;
import org.grapheus.web.state.RepresentationState;
import org.grapheus.web.state.VertexListFilter;

/**
 * @author black
 */
public class VerticesFilterPanel extends Panel {
	private static final long serialVersionUID = 1L;
	private final RepresentationState representationState;
	private final SerializableConsumer<IPartialPageRequestHandler> filterChangedCallback;
	
	@Builder
	public VerticesFilterPanel(String id,
	        RepresentationState representationState,
	        SerializableConsumer<IPartialPageRequestHandler> filterChangedCallback) {
		super(id);
		this.representationState = representationState;;
		this.filterChangedCallback = filterChangedCallback;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		AdvancedFilterDialog filterDialog = createAdvancedFilterDialog("filterDialog");
		add(filterDialog);
		
		add(createForm("filter_form")
				.add(createSubstringInput("substring"))
				.add(createAdvancedFilterForm("filterDialogLink", filterDialog)));
	}
	
	private AdvancedFilterDialog createAdvancedFilterDialog(String dialogDivId) {
		return AdvancedFilterDialog.builder()
		        .id(dialogDivId)
		        .title("Advanced Filter")
		        .graphIdSupplier(()->representationState.getGraphId())
		        .verticesListFilter(representationState.getVertexListFilter())
		        .filterAppliedCallback(filterChangedCallback::accept)
		        .build();
	}

	private Component createAdvancedFilterForm(String id, AdvancedFilterDialog filterDialog) {
		return new LambdaAjaxLink(id, target->filterDialog.open(target));
	}

	private Component createSubstringInput(String id) {
		return new LambdaAjaxTextField<String>(id, filterChangedCallback);
	}

	private WebMarkupContainer createForm(String id) {
		return new Form<VertexListFilter>(id,
                new CompoundPropertyModel<>(representationState.getVertexListFilter()));
	}

	
}
