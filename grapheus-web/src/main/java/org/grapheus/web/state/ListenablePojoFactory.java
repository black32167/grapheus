package org.grapheus.web.state;

import lombok.RequiredArgsConstructor;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class ListenablePojoFactory<T> {
    @FunctionalInterface
    interface ModelUpdateListener<T> {
        void onUpdate(T object);
    }

    private final Map<String, List<ModelUpdateListener<T>>> updateListeners = new HashMap<>();
    private final Class<T> targetClass;

    public ListenablePojoFactory<T> addUpdateListener(String propertyName, ModelUpdateListener<T> listener) {
        updateListeners.computeIfAbsent(propertyName, (k)->new ArrayList<>()).add(listener);
        return this;
    }

    public T create() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(targetClass);
        enhancer.setCallback(new ListeningMethodInterceptor());
        return (T) enhancer.create();
    }

    class ListeningMethodInterceptor implements MethodInterceptor {
        public Object intercept(Object obj, Method method, Object[] args,
                                MethodProxy proxy) throws Throwable {
            Object result = proxy.invoke(obj, args);
            if(isSetter(method)) {
                String property = toProperty(method);
                updateListeners.get(property).forEach(l->l.onUpdate((T)obj));
            }
            return result;
        }

        private String toProperty(Method method) {
            String name = method.getName();
            return name.substring(3,4).toUpperCase() + name.substring(4);
        }

        boolean isSetter(Method method) {
           String name = method.getName();
           return name.startsWith("set");
        }
    }
}
