package org.grapheus.web.state;

import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListeningCompoundModel<T> extends CompoundPropertyModel<T> {
    @FunctionalInterface
    interface ModelUpdateListener {
        void onUpdate(String property, Object value);
    }
    private final Map<String, List<ModelUpdateListener>> updateListeners = new HashMap<>();

    public ListeningCompoundModel(T object) {
        super(object);
    }

    @Override
    public <S> IModel<S> bind(String property) {
        return new IModel<S>() {
            final IModel<S> delegate;
            {
                delegate = ListeningCompoundModel.super.bind(property);
            }

            @Override
            public void detach() {
                delegate.detach();
            }

            @Override
            public S getObject() {
                return delegate.getObject();
            }

            @Override
            public void setObject(S object) {
                updateListeners.get(property).forEach(l->l.onUpdate(property, object));
                delegate.setObject(object);
            }
        };
    }

    public ListeningCompoundModel addUpdateListener(String propertyName, ModelUpdateListener listener) {
        updateListeners.computeIfAbsent(propertyName, (k)->new ArrayList<>()).add(listener);
        return this;
    }
}
