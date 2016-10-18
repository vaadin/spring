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

import org.springframework.context.ApplicationContext;

import com.vaadin.spring.annotation.ViewContainer;

/**
 * Dynamically registered bean which can provide a reference to the current view
 * container instance.
 *
 * @see ViewContainer
 * @see ViewContainerPostProcessor
 *
 * @author Vaadin Ltd
 */
public class ViewContainerRegistrationBean {

    private Class<?> beanClass;

    public Object getViewContainer(ApplicationContext applicationContext) {
        // get the bean of the correct class from the context
        return applicationContext.getBean(beanClass);
    }

    /**
     * Set the class of the bean that is used to find the view container.
     *
     * @param beanClass
     *            class of the bean that contains the ViewContainer annotation
     *            or has it directly on the class
     */
    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

}
