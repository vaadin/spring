/*
 * Copyright 2015-2026 The original authors
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
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import java.io.Serializable;
import java.security.Principal;
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

    private static boolean caseSensitiveRoleDefs = false;
    
    @Autowired
    private transient ApplicationContext applicationContext;

    /**
     * The behavior of Spring Security up to version 6.x allowed for case-insensitive
     * role definitions. Spring boot 7 changes this to case-sensitive by default.
     * Spring Add-on for Vaadin 8 retains the old behavior of case insensitive role
     * names by default, but the new behavior can be enabled by calling this function
     * with the parameter 'true'.
     * 
     * @param enabled true to enable case sensitive role definitions.
     */
    public static void setCaseSensitive(boolean enabled) {
        caseSensitiveRoleDefs = enabled;
    }

    /**
     * Returns whether role-definition matching is currently case-sensitive.
     * <p>
     * By default this is {@code true}. The value can be changed through
     * {@link #setCaseSensitive(boolean)}.
     *
     * @return {@code true} when role definitions are matched case-sensitively,
     *         {@code false} when matching is case-insensitive
     */
    public static boolean isCaseSensitive() {
        return caseSensitiveRoleDefs;
    }

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
        Authentication authentication = resolveAuthentication();
        if (authentication == null) {
            return false;
        }

        Set<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        if (caseSensitiveRoleDefs) {
            return Stream.of(securityConfigAttributes)
                .anyMatch(authorities::contains);
        }

        Set<String> normalizedAuthorities = authorities.stream()
                .map(SecuredViewAccessControl::normalizeAuthority)
                .collect(Collectors.toSet());

        return Stream.of(securityConfigAttributes)
                .map(SecuredViewAccessControl::normalizeAuthority)
                .anyMatch(normalizedAuthorities::contains);
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
    public boolean isAccessGranted(Class<? extends View> viewClass) {
        Secured viewSecured = null;
        if (viewClass != null) {
            viewSecured = AnnotationUtils.findAnnotation(viewClass, Secured.class);
        }
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

    private Authentication resolveAuthentication() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null && context.getAuthentication() != null) {
            return context.getAuthentication();
        }

        VaadinRequest request = VaadinService.getCurrentRequest();
        if (request instanceof VaadinServletRequest servletRequest) {
            Principal principal = servletRequest.getUserPrincipal();
            if (principal instanceof Authentication authentication) {
                return authentication;
            }

            HttpSession session = servletRequest.getHttpServletRequest()
                    .getSession(false);
            if (session != null) {
                Object securityContextAttribute = session.getAttribute(
                        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
                if (securityContextAttribute instanceof SecurityContext securityContext) {
                    return securityContext.getAuthentication();
                }
            }
        }

        return null;
    }

    /**
     * Helper for case insensitivity
     */
    private static String normalizeAuthority(String authority) {
        return authority == null ? "" : authority.trim().toUpperCase();
    }

}
