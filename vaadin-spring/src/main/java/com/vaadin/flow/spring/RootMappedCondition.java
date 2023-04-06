/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.spring;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.web.servlet.DispatcherServlet;

import com.vaadin.flow.server.VaadinServlet;

/**
 * Condition to check whether the Vaadin servlet is mapped to the root
 * ({@literal "/*"}).
 * <p>
 * In this case a {@link DispatcherServlet} is used. It's mapped to the root
 * instead of VaadinServlet and forwards requests to {@link VaadinServlet}. If
 * there are other mappings (via Spring endpoints e.g.) then
 * {@link DispatcherServlet} makes it possible to handle them properly via those
 * endpoints. Otherwise {@link VaadinServlet} will handle all the URLs because
 * it has the highest priority.
 *
 * @author Vaadin Ltd
 *
 */
public class RootMappedCondition implements Condition {

    public static final String URL_MAPPING_PROPERTY = "vaadin.urlMapping";

    @Override
    public boolean matches(ConditionContext context,
            AnnotatedTypeMetadata metadata) {
        return isRootMapping(
                context.getEnvironment().getProperty(URL_MAPPING_PROPERTY));
    }

    /**
     * Returns {@code true} if {@code mapping} is the root mapping
     * ({@literal "/*"}).
     * <p>
     * The mapping is controlled via the {@code vaadin.urlMapping} property
     * value. By default it's {@literal "/*"}.
     *
     * @param mapping
     *            the mapping string to check
     * @return {@code true} if {@code mapping} is the root mapping and
     *         {@code false} otherwise
     */
    public static boolean isRootMapping(String mapping) {
        if (mapping == null) {
            return true;
        }
        return mapping.trim().replaceAll("(/\\**)?$", "").isEmpty();
    }
}
