/**
 * 
 */
package grapheus.persistence.graph.generate;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import grapheus.absorb.VertexPersister;
import grapheus.persistence.exception.GraphExistsException;
import grapheus.persistence.model.graph.PersistentVertex;
import grapheus.view.SemanticFeature;
import grapheus.view.extract.features.SemanticFeatureType;

/**
 * Uploads own dependency graph to the database at startup.
 * 
 * @author black
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__({ @Inject }))
public class SelfGraphGenerator {
    private final ConfigurableListableBeanFactory appCtx;
    private final VertexPersister vertexPersister;
    private final EmptyGraphGenerator emptyGraphGenerator;//Should we use it from an other generators?

    public void generate(String grapheusUserKey, String graphName) throws GraphExistsException {

        emptyGraphGenerator.createGraph(grapheusUserKey, graphName);
        
        String[] beanNames = appCtx.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            
            // if(beanDefinition.isSingleton()...
            Object bean = unProxy(appCtx.getBean(beanName));
            Class<?> beanClass = unCGLib(bean.getClass());
            log.info("Processing bean '{}'", beanClass.getCanonicalName());
            Collection<Class<?>> dependencies = collectDependencies(bean);
            vertexPersister.update(graphName,
                    PersistentVertex.builder().id(beanClass.getCanonicalName())
                            .title(beanClass.getSimpleName()).description(beanClass.getSimpleName())
                            .semanticFeatures(dependenciesToFeatures(dependencies)).build());
        }

        log.info("Processed {} beans!", beanNames.length);
        
        log.info("Graph {} is created", graphName);

    }

    private Object unProxy(Object bean) {
        if(AopUtils.isAopProxy(bean) && bean instanceof Advised) {
            Advised advised = (Advised) bean;
            
            try {
                return advised.getTargetSource().getTarget();
            } catch (Exception e) {
              log.error("Could not unproxy bean:{}", bean, e);
            }
        }

        if(bean.getClass().getName().contains("Proxy")) {
            log.error("Could not unproxy bean {}", bean);
        }
        return bean;
    }

    private List<SemanticFeature> dependenciesToFeatures(Collection<Class<?>> dependencies) {
        return dependencies.stream()
                .map(c -> SemanticFeature.from(SemanticFeatureType.LOCAL_ID_REFERENCE, c.getCanonicalName()))
                .collect(Collectors.toList());
    }

    private Collection<Class<?>> collectDependencies(@NonNull Object bean) {
        Set<Class<?>> dependencies = new HashSet<Class<?>>();
        Class<?> beanClass = bean.getClass();

        // Look for injects via the constructor
        for (Constructor<?> c : beanClass.getConstructors()) {
            if (c.isAnnotationPresent(Inject.class)) {
                Type constructorArgumentTypes[] = c.getGenericParameterTypes();
                log.info(">> {}({})", beanClass.getSimpleName(), Arrays.toString(constructorArgumentTypes));
                for (Type genericType : constructorArgumentTypes) {
                    if (genericType instanceof Class) {
                        Class<?> parameterClass = (Class<?>) genericType;
                        Object dependency = null;
                        try {
                            dependency = appCtx.getBean(parameterClass);
                            dependencies.add(unProxy(dependency).getClass());
                        } catch (NoSuchBeanDefinitionException e) {
                            log.warn("Cannot retrieve bean of type {} for constructor of the class {}", parameterClass, beanClass);
                        }
                    } else if(genericType instanceof ParameterizedType) {
                        ParameterizedType parameterizedType = (ParameterizedType) genericType;
                        Type rawType = parameterizedType.getRawType();
                        if (rawType instanceof Class && Collection.class.isAssignableFrom((Class)rawType)) {
                            Type collectionTypeParameters[] = parameterizedType.getActualTypeArguments();
                            if(collectionTypeParameters.length > 0 && collectionTypeParameters[0] instanceof Class) {
                                Class<?> dependencyBeanClass = (Class<?>) collectionTypeParameters[0];
                                Map<String, Object> foundBeans = (Map<String, Object>) appCtx.getBeansOfType(dependencyBeanClass);
                                for(Object foundBean:foundBeans.values()) {
                                    dependencies.add(unProxy(foundBean).getClass());
                                }
                            }
                        }
                    }
                }
            }
        }

        // Look for dependencies on fields
        while (beanClass != Object.class) {
            for (Field f : beanClass.getDeclaredFields()) {
                if (f.isAnnotationPresent(Inject.class) || f.isAnnotationPresent(Autowired.class)) {
                    try {
                        f.setAccessible(true);
                        Object dependency = f.get(bean);
                        if(dependency instanceof Collection) {
                            for(Object element:(Collection<?>)dependency) {
                                dependencies.add(unProxy(element).getClass());
                            }
                        } else if(dependency != null) {
                            dependencies.add(unProxy(dependency).getClass());
                        }
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        log.error("Cannot get value of bean field:{}::{}", bean.getClass().getName(), f.getName());
                    }
                }
            }
            beanClass = beanClass.getSuperclass();
        }

        return dependencies.stream().map(this::unCGLib).collect(Collectors.toList());
    }
    
    private Class<?> unCGLib(Class<?> sourceClass) {
        while(sourceClass.getName().contains("$$")) {
            sourceClass = sourceClass.getSuperclass();
        }
        return sourceClass;
    }

}
