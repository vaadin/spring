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

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultBeanNameGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.vaadin.navigator.ViewDisplay;
import com.vaadin.spring.annotation.ViewContainer;
import com.vaadin.spring.server.SpringUIProvider;
import com.vaadin.ui.Component;

/**
 * Bean post processor that scans for {@link ViewContainer} annotations on UI
 * scoped beans or bean classes and registers
 * {@link ViewContainerRegistrationBean} instances for them for
 * {@link SpringUIProvider}.
 *
 * @author Vaadin Ltd
 */
public class ViewContainerPostProcessor
        implements BeanPostProcessor, ApplicationContextAware {
    private transient Set<Class<?>> classesWithoutViewContainerAnnotation = new HashSet<Class<?>>();
    private ApplicationContext applicationContext;

    private BeanNameGenerator beanNameGenerator = new DefaultBeanNameGenerator();

    @Override
    public Object postProcessAfterInitialization(final Object bean,
            String beanName) throws BeansException {
        final Class<?> clazz = bean.getClass();
        // TODO optimize by prescanning to create a whitelist?
        boolean classOk = Component.class.isAssignableFrom(clazz)
                || ViewDisplay.class.isAssignableFrom(clazz);
        if (!classOk || classesWithoutViewContainerAnnotation.contains(clazz)) {
            return bean;
        }
        // if not UI scoped, cannot have a valid @ViewContainer
        if (applicationContext instanceof ConfigurableListableBeanFactory) {
            BeanDefinition beanDefinition = ((ConfigurableListableBeanFactory) applicationContext)
                    .getBeanDefinition(beanName);
            String scope = beanDefinition.getScope();
            if (!UIScopeImpl.VAADIN_UI_SCOPE_NAME.equals(scope)) {
                return bean;
            }
            // TODO look for annotations on factory methods
        }
        // look for annotations on classes
        if (clazz.isAnnotationPresent(ViewContainer.class)) {
            registerViewContainerBean(clazz);
        } else {
            classesWithoutViewContainerAnnotation.add(clazz);
        }
        return bean;
    }

    /**
     * Create a view container registration bean definition to allow accessing
     * annotated view containers for the current UI scope.
     *
     * @param clazz
     *            bean class having the view container annotation, not null
     */
    protected void registerViewContainerBean(Class<?> clazz) {
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) applicationContext;
        BeanDefinitionBuilder builder = BeanDefinitionBuilder
                .genericBeanDefinition(ViewContainerRegistrationBean.class);

        // information needed to extract the values from the current UI scoped
        // beans
        builder.addPropertyValue("beanClass", clazz);

        builder.setScope(UIScopeImpl.VAADIN_UI_SCOPE_NAME);
        builder.setRole(BeanDefinition.ROLE_SUPPORT);
        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        String name = getBeanNameGenerator().generateBeanName(beanDefinition,
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

    public BeanNameGenerator getBeanNameGenerator() {
        return beanNameGenerator;
    }

    public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
        this.beanNameGenerator = beanNameGenerator;
    }

}
