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

import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private transient ApplicationContext applicationContext;

    /**
     * Checks if the current user is granted any explicitly provided security attributes
     * (usually set with {@code @Secured} annotation).
     *
     * @param securityConfigAttributes
     *          list of security configuration attributes (e.g. ROLE_USER, ROLE_ADMIN).
     * @return {@code true} if the access is granted or the view is not secured, {@code false} otherwise
     * @see Secured
     */
    protected boolean isAccessGranted(String[] securityConfigAttributes) {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        if (authentication == null) {
            return false;
        }
        Set<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        return
                Stream.of(securityConfigAttributes).anyMatch(authorities::contains);
    }

    /**
     * Checks the explicitly given bean of {@code View} class for granted access for the current user
     *
     * @param ui       current UI
     * @param beanName view bean name
     * @return {@code true} if the access is granted or the view is not secured, {@code false} otherwise
     * @see Secured
     */
    @Override
    public boolean isAccessGranted(UI ui, String beanName) {
        final Secured viewSecured = getApplicationContext(ui).findAnnotationOnBean(beanName, Secured.class);
        return isAccessGranted(viewSecured);
    }

    /**
     * Checks the explicitly given {@code View} class for granted access for the current user
     *
     * @param viewClass view class
     * @return {@code true} if the access is granted or the view is not secured, {@code false} otherwise
     * @see Secured
     */
    @SuppressWarnings("unused")
    public boolean isAccessGranted(Class<? extends View> viewClass) {
        Secured viewSecured = AnnotationUtils.findAnnotation(viewClass, Secured.class);
        return isAccessGranted(viewSecured);
    }

    /**
     * Checks the explicitly given view annotation for granted access for the current user
     *
     * @param viewSecured annotation instance detected on a {@link View}
     * @return {@code true} if the access is granted or the view is not secured, {@code false} otherwise
     */
    @SuppressWarnings("WeakerAccess")
    protected boolean isAccessGranted(Secured viewSecured) {
        if (viewSecured == null) {
            return true;
        } else {
            return isAccessGranted(viewSecured.value());
        }
    }

    private ApplicationContext getApplicationContext(UI ui) {
        if (applicationContext == null) {
            applicationContext = ((SpringVaadinServletService) ui.getSession().getService())
                    .getWebApplicationContext();
        }

        return applicationContext;
    }
}
