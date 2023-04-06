/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.spring.annotation;

import static com.vaadin.flow.spring.scopes.VaadinUIScope.VAADIN_UI_SCOPE_NAME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Scope;

/**
 * Stereotype annotation for Spring's {@code @Scope("vaadin-ui")}.
 *
 * @author Vaadin Ltd
 */
@Scope(VAADIN_UI_SCOPE_NAME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UIScope {
}
