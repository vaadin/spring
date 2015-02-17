/*
 * Copyright 2015 The original authors
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
package com.vaadin.spring.boot.config;

import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.vaadin.spring.servlet.SpringAwareVaadinServlet;

/**
 * Spring configuration that sets up a
 * {@link com.vaadin.spring.servlet.SpringAwareVaadinServlet}. If you want to
 * customize the servlet, extend it and make it available as a Spring bean.
 *
 * @author Petter Holmström (petter@vaadin.com)
 * @author Henri Sara (hesara@vaadin.com)
 */
@Configuration
public class VaadinServletConfiguration extends AbstractServletConfiguration {

    private static Logger logger = LoggerFactory
            .getLogger(VaadinServletConfiguration.class);

    /**
     * Prefix to be used for all Spring environment properties that configure
     * the Vaadin servlet. The full format of the environment property name is
     * {@code [prefix][initParameter]} where {@code [prefix]} is
     * <code>{@value}</code> and {@code initParameter} is the name of one of the
     * parameters defined in
     * {@link com.vaadin.annotations.VaadinServletConfiguration}.
     * <p>
     * For example, to change the production mode of the servlet, a property
     * named <code>{@value}productionMode</code> would be used.
     *
     * @see org.springframework.core.env.Environment
     */
    public static final String SERVLET_CONFIGURATION_PARAMETER_PREFIX = "vaadin.servlet.params.";

    /**
     * Name of the Spring environment property that contains the base URL
     * mapping of the Vaadin servlet. By default, this mapping is {@code /*}.
     *
     * If a value other than {@code /*} is used, also {@code /VAADIN/*} is
     * automatically mapped to the same servlet.
     */
    public static final String SERVLET_URL_MAPPING_PARAMETER_NAME = "vaadin.servlet.urlMapping";

    @Override
    protected String getServletConfigurationParameterPrefix() {
        return SERVLET_CONFIGURATION_PARAMETER_PREFIX;
    }

    @Override
    protected Class<? extends HttpServlet> getServletClass() {
        return SpringAwareVaadinServlet.class;
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected String[] getUrlMappings() {
        String mapping = environment
                .getProperty(SERVLET_URL_MAPPING_PARAMETER_NAME,
                        DEFAULT_SERVLET_URL_MAPPING);
        if (!DEFAULT_SERVLET_URL_MAPPING.equals(mapping)) {
            return new String[] { mapping, STATIC_RESOURCES_URL_MAPPING };
        } else {
            return new String[] { mapping };
        }
    }

    @Bean
    ServletRegistrationBean vaadinServletRegistration() {
        return createServletRegistrationBean();
    }

}
