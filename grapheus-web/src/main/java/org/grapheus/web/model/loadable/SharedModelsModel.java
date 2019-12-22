package org.grapheus.web.model.loadable;

import lombok.RequiredArgsConstructor;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.grapheus.web.state.GlobalFilter;
import org.grapheus.web.state.SharedModels;

@RequiredArgsConstructor
public class SharedModelsModel extends LoadableDetachableModel<SharedModels> {
    private final IModel<GlobalFilter> filterModel;

    @Override
    protected SharedModels load() {
        return new SharedModels(filterModel.getObject());
    }
}
