/*
 * Copyright 2000-2018 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.flow.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Creates a {@link DispatcherServletRegistrationBean} instance for a dispatcher
 * servlet in case Vaadin servlet is mapped to the root.
 * <p>
 * This is a workaround for spring boot 2.0.4 compatibility (see spring#331).
 *
 * @see #postProcessBeforeInitialization(Object, String)
 *
 * @author Vaadin Ltd
 *
 */
@Component
@Conditional(RootMappedCondition.class)
@ConditionalOnClass(DispatcherServletRegistrationBean.class)
public class ServletRegistrationPostProcessor
        implements BeanPostProcessor {
    @Autowired
    private WebApplicationContext context;

    /**
     * Replaces a default {@link DispatcherServletRegistrationBean} instance for
     * a dispatcher servlet in case Vaadin servlet is mapped to the root.
     * <p>
     * This is needed for correct servlet path (and path info) values available
     * in Vaadin servlet because it works via forwarding controller which is not
     * properly mapped without this registration.
     *
     * @return a custom DispatcherServletRegistrationBean instance for
     *         dispatcher servlet
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if (bean instanceof DispatcherServletRegistrationBean) {
            DispatcherServletRegistrationBean oldBean = (DispatcherServletRegistrationBean) bean;
            if ("/".equals(oldBean.getPath())) {
                DispatcherServletRegistrationBean registration = new DispatcherServletRegistrationBean(
                        context.getBean(DispatcherServlet.class), "/*");
                registration.setName("dispatcher");
                return registration;
            }
        }
        return bean;
    }
}
