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
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.server.SpringVaadinServletService;
import com.vaadin.ui.UI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.WebApplicationContext;

import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Bean to enable Spring Security support for {@link com.vaadin.spring.navigator.SpringViewProvider SpringViewProvider}.
 * The bean should be visible for component scan.
 * <p>
 * If the bean is used, application {@link View Views} can be protected against unauthorized access with
 * the {@link Secured @Secured} annotation.
 */
@SuppressWarnings("WeakerAccess")
@SpringComponent
public class SecuredViewAccessControl implements ViewAccessControl, Serializable {

    @Autowired
    private WebApplicationContext webApplicationContext;

    protected boolean isAccessGranted(UI ui, String[] securityConfigurationAttributes) {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        if (authentication == null) {
            return false;
        }
        Set<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        for (String attribute : securityConfigurationAttributes) {
            if (authorities.contains(attribute)) {
                return true;
            }
        }
        return false;
    }

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
     * @see Secured
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

    private WebApplicationContext getWebApplicationContext(UI ui) {
        if (webApplicationContext == null) {
            webApplicationContext = ((SpringVaadinServletService) ui.getSession().getService())
                    .getWebApplicationContext();
        }

        return webApplicationContext;
    }
}
