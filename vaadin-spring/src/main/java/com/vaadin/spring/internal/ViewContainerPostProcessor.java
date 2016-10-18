/*
 * Copyright 2015-2016 The original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vaadin.spring.internal;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultBeanNameGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

import com.vaadin.spring.annotation.ViewContainer;

public class ViewContainerPostProcessor
        implements BeanPostProcessor, ApplicationContextAware {
    private transient Set<Class<?>> classesWithoutViewContainerAnnotation = new HashSet<Class<?>>();
    private ApplicationContext applicationContext;

    private BeanNameGenerator beanNameGenerator = new DefaultBeanNameGenerator();

    @Override
    public Object postProcessAfterInitialization(final Object bean,
            String beanName) throws BeansException {
        final Class<?> clazz = bean.getClass();
        // TODO optimize not to scan every bean class
        // TODO do this only for UI scoped beans and smaller
        if (classesWithoutViewContainerAnnotation.contains(clazz)) {
            return bean;
        }
        final boolean[] found = {
                clazz.isAnnotationPresent(ViewContainer.class) };
        // TODO register view container bean?
        ReflectionUtils.doWithFields(clazz, new FieldCallback() {
            @Override
            public void doWith(Field field)
                    throws IllegalArgumentException, IllegalAccessException {
                if (field.isAnnotationPresent(ViewContainer.class)) {
                    found[0] = true;

                    // add a UI scoped bean definition
                    registerViewContainerBean(clazz, field);
                }
            }
        });
        if (!found[0]) {
            classesWithoutViewContainerAnnotation.add(clazz);
        }
        return bean;
    }

    private void registerViewContainerBean(Class<?> clazz, Field field) {
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) applicationContext;
        BeanDefinitionBuilder builder = BeanDefinitionBuilder
                .genericBeanDefinition(ViewContainerRegistrationBean.class);

        // information needed to extract the values from the current UI scoped
        // beans
        builder.addPropertyValue("beanClass", clazz);
        builder.addPropertyValue("field", field);

        builder.setScope(UIScopeImpl.VAADIN_UI_SCOPE_NAME);
        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        String name = beanNameGenerator.generateBeanName(beanDefinition,
                registry);
        registry.registerBeanDefinition(name, beanDefinition);
    }

    @Override
    public Object postProcessBeforeInitialization(final Object bean,
            String beanName) throws BeansException {
        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

}
