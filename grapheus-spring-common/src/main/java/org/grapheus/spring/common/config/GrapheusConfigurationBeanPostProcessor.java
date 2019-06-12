/**
 * 
 */
package org.grapheus.spring.common.config;

import java.lang.reflect.Field;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.env.PropertyResolver;
import org.springframework.util.ReflectionUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * @author black
 */
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
@Slf4j
public class GrapheusConfigurationBeanPostProcessor implements BeanPostProcessor {
	private final PropertyResolver propertyResolver;

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		ReflectionUtils.doWithFields(bean.getClass(), field->injectConfiguration(field, bean));
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}
	
	private void injectConfiguration(Field field, Object bean) {
		Optional.ofNullable(field.getAnnotation(ConfigProperty.class))
			.map(config->config.value())
			.ifPresent(propKey-> {
				Object propValue = propertyResolver.getProperty(propKey, field.getType());
				field.setAccessible(true);
				try {
					field.set(bean, propValue);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					log.error("Cannot inject property '{}'", propKey, e);
				}
			});

	}

}
