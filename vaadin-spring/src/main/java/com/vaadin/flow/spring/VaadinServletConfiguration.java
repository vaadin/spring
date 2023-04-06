/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.spring;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.mvc.ServletForwardingController;

import com.vaadin.flow.server.VaadinServlet;

/**
 * Vaadin servlet configuration.
 * <p>
 * The configuration is used only when the Vaadin servlet is mapped to the root
 * ({@literal "/*"}) because in this case the servlet is mapped to
 * {@literal "/vaadinServlet/*"} instead of ({@literal "/*"}). It's done to make
 * possible to configure other Spring services (like endpoints) which have
 * overlapping path.
 *
 *
 * @author Vaadin Ltd
 *
 */
@Configuration
@Conditional(RootMappedCondition.class)
public class VaadinServletConfiguration {

    static final String VAADIN_SERVLET_MAPPING = "/vaadinServlet/*";

    /**
     * Makes an url handler mapping allowing to forward requests from a
     * {@link DispatcherServlet} to {@link VaadinServlet}.
     *
     * @return an url handler mapping instance which forwards requests to vaadin
     *         servlet
     */
    @Bean
    public SimpleUrlHandlerMapping vaadinRootMapping() {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(Ordered.LOWEST_PRECEDENCE - 1);

        mapping.setUrlMap(
                Collections.singletonMap("/*", vaadinForwardingController()));

        return mapping;
    }

    /**
     * Makes a forwarding controller.
     *
     * @return a forwarding controller
     */
    @Bean
    public Controller vaadinForwardingController() {
        ServletForwardingController controller = new ServletForwardingController();
        controller.setServletName(
                ClassUtils.getShortNameAsProperty(SpringServlet.class));
        return controller;
    }

}
