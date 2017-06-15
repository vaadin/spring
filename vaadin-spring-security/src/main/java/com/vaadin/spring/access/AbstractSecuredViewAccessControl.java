/*
 * Copyright 2015-2017 The original authors
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
package com.vaadin.spring.access;

import com.vaadin.navigator.View;
import com.vaadin.spring.server.SpringVaadinServletService;
import com.vaadin.ui.UI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.context.WebApplicationContext;

import java.io.Serializable;

/**
 * Abstract class to enable Spring Security support for
 * {@link com.vaadin.spring.navigator.SpringViewProvider SpringViewProvider}.
 * The implementation of the class should be marked with
 * {@link com.vaadin.spring.annotation.SpringComponent @SpringComponent} annotation and be visible for component scan.
 * <p>
 * If the implementation of the class is used, application {@link View Views} can be protected against unauthorized access with
 * the {@link Secured @Secured} annotation.
 */
public abstract class AbstractSecuredViewAccessControl implements ViewAccessControl, Serializable {

    @Autowired
    private WebApplicationContext webApplicationContext;

    /**
     * Checks the explicitly given bean of {@code View} class for granted access for the current user
     *
     * @param ui       current UI
     * @param beanName view bean name
     * @return {@code true} if the access is granted or the view is not secured, {@code false} otherwise
     * @see org.springframework.security.access.annotation.Secured
     */
    @Override
    public boolean isAccessGranted(UI ui, String beanName) {
        final Secured viewSecured = getWebApplicationContext(ui).findAnnotationOnBean(beanName, Secured.class);
        return isAccessGranted(ui, viewSecured);
    }

    /**
     * Checks the explicitly given {@code View} class for granted access for the current user
     *
     * @param ui        current UI
     * @param viewClass view class
     * @return {@code true} if the access is granted or the view is not secured, {@code false} otherwise
     * @see org.springframework.security.access.annotation.Secured
     */
    @SuppressWarnings("unused")
    public boolean isAccessGranted(UI ui, Class<? extends View> viewClass) {
        Secured viewSecured = AnnotationUtils.findAnnotation(viewClass, Secured.class);
        return isAccessGranted(ui, viewSecured);
    }

    /**
     * Checks the explicitly given view annotation for granted access for the current user
     *
     * @param ui          current UI
     * @param viewSecured annotation instance detected on a {@link View}
     * @return {@code true} if the access is granted or the view is not secured, {@code false} otherwise
     */
    @SuppressWarnings("WeakerAccess")
    protected boolean isAccessGranted(UI ui, Secured viewSecured) {
        if (viewSecured == null) {
            return true;
        } else {
            return isAccessGranted(ui, viewSecured.value());
        }
    }

    /**
     * Checks the security attributes found for a view for the current user
     *
     * @param ui
     *          current UI
     * @param securityConfigurationAttributes
     *          attributes of the view
     * @return {@code true} if the access is granted or the view is not secured, {@code false} otherwise
     * @see org.springframework.security.access.annotation.Secured
     */
    protected abstract boolean isAccessGranted(UI ui, String securityConfigurationAttributes[]);

    private WebApplicationContext getWebApplicationContext(UI ui) {
        if (webApplicationContext == null) {
            webApplicationContext = ((SpringVaadinServletService) ui.getSession().getService())
                    .getWebApplicationContext();
        }

        return webApplicationContext;
    }
}
