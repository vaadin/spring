/*
 * Copyright 2000-2021 Vaadin Ltd.
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
package com.vaadin.flow.spring.scopes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.ExtendedClientDetails;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationListener;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterListener;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.SpringVaadinSession;
import com.vaadin.flow.spring.annotation.RouteScopeOwner;

/**
 * Implementation of Spring's
 * {@link org.springframework.beans.factory.config.Scope} that binds the beans
 * to a component in the navigation chain. Registered by default as the scope "
 * {@value #VAADIN_ROUTE_SCOPE_NAME}".
 *
 * @see com.vaadin.flow.spring.annotation.VaadinSessionScope
 * @author Vaadin Ltd
 * @since
 *
 */
public class VaadinRouteScope extends AbstractScope {

    public static final String VAADIN_ROUTE_SCOPE_NAME = "vaadin-route";

    private static class RouteStoreWrapper {

        private final VaadinSession session;

        private final Registration sessionDestroyListenerRegistration;

        private final Map<String, BeanStore> routeStores;

        private final ApplicationContext context;

        private RouteStoreWrapper(VaadinSession session,
                ApplicationContext context) {
            assert session.hasLock();
            this.session = session;
            this.context = context;
            routeStores = new HashMap<>();
            if (session instanceof SpringVaadinSession) {
                sessionDestroyListenerRegistration = null;
                ((SpringVaadinSession) session)
                        .addDestroyListener(event -> destroy());
            } else {
                sessionDestroyListenerRegistration = session.getService()
                        .addSessionDestroyListener(event -> destroy());
            }
        }

        private BeanStore getBeanStore(UI ui) {
            assert session.hasLock();
            ExtendedClientDetails details = ui.getInternals()
                    .getExtendedClientDetails();
            String key;
            if (details == null) {
                key = "uid-" + ui.getUIId();
                ui.getPage().retrieveExtendedClientDetails(
                        det -> relocateStore(ui));
            } else {
                key = "win-" + getWindowName(ui);
            }
            BeanStore beanStore = routeStores.get(key);
            if (beanStore == null) {
                beanStore = new RouteBeanStore(ui, session, context);
                routeStores.put(key, beanStore);
            }
            return beanStore;
        }

        private void relocateStore(UI ui) {
            assert session.hasLock();
            String key = String.valueOf(ui.getUIId());
            BeanStore beanStore = routeStores.remove(key);
            if (beanStore == null) {

            } else {
                routeStores.put(getWindowName(ui), beanStore);
            }
        }

        private void destroy() {
            session.lock();
            try {
                session.setAttribute(RouteStoreWrapper.class, null);
            } finally {
                session.unlock();
                if (sessionDestroyListenerRegistration != null) {
                    sessionDestroyListenerRegistration.remove();
                }
            }
        }

    }

    private static class RouteBeanStore extends BeanStore
            implements ComponentEventListener<DetachEvent>,
            AfterNavigationListener, BeforeEnterListener {

        private final ApplicationContext context;

        private UI currentUI;

        private Registration uiDetachRegistration;
        private Registration afterNavigationRegistration;
        private Registration beforeEnterRegistration;

        private Class<?> currentNavigationTarget;
        private List<Class<? extends RouterLayout>> currentLayouts;

        private Map<Class<?>, Set<String>> beanNamesByNavigationComponents = new HashMap<>();

        private RouteBeanStore(UI ui, VaadinSession session,
                ApplicationContext context) {
            super(session);
            this.context = context;
            currentUI = ui;
            addListeners();
        }

        @Override
        protected Object doGet(String name, ObjectFactory<?> objectFactory) {
            RouteScopeOwner owner = context.findAnnotationOnBean(name,
                    RouteScopeOwner.class);
            if (owner == null) {
                return objectFactory.getObject();
            }
            if (hasOwnerType(currentNavigationTarget, owner)
                    || layoutsContainsOwner(owner)) {
                LoggerFactory.getLogger(RouteBeanStore.class).error(
                        "Route owner '%s' instance is not available in the "
                                + "active navigaiton components chain: the scope defined by the bean '%s' doesn't exist.",
                        owner.value(), name);
                return null;
            }
            return super.doGet(name, objectFactory);
        }

        @Override
        protected void storeBean(String name, Object bean) {
            super.storeBean(name, bean);
            RouteScopeOwner owner = context.findAnnotationOnBean(name,
                    RouteScopeOwner.class);
            Class<? extends HasElement> clazz = owner.value();
            Set<String> set = beanNamesByNavigationComponents
                    .computeIfAbsent(clazz, key -> new HashSet<>());
            set.add(name);
        }

        private boolean resetUI() {
            for (UI ui : getVaadinSession().getUIs()) {
                String windowName = getWindowName(ui);
                if (ui != currentUI
                        && windowName.equals(getWindowName(currentUI))) {
                    currentUI = ui;
                    return true;
                }
            }
            return false;
        }

        @Override
        public void onComponentEvent(DetachEvent event) {
            assert getVaadinSession().hasLock();
            uiDetachRegistration.remove();
            afterNavigationRegistration.remove();
            beforeEnterRegistration.remove();
            if (resetUI()) {
                addListeners();
            } else {
                destroy();
            }
        }

        @Override
        public void afterNavigation(AfterNavigationEvent event) {
            Map<Class<?>, Set<String>> activeChain = new HashMap<>();
            event.getActiveChain().stream().map(Object::getClass)
                    .forEach(clazz -> putIfNotNull(activeChain, clazz,
                            beanNamesByNavigationComponents.remove(clazz)));

            beanNamesByNavigationComponents.values()
                    .forEach(names -> names.forEach(this::remove));
            beanNamesByNavigationComponents = activeChain;
        }

        @Override
        public void beforeEnter(BeforeEnterEvent event) {
            currentNavigationTarget = event.getNavigationTarget();
            currentLayouts = event.getLayouts();

            Map<Class<?>, Set<String>> activeChain = new HashMap<>();
            putIfNotNull(activeChain, currentNavigationTarget,
                    beanNamesByNavigationComponents
                            .remove(currentNavigationTarget));
            currentLayouts.forEach(layoutClass -> putIfNotNull(activeChain,
                    layoutClass,
                    beanNamesByNavigationComponents.remove(layoutClass)));

            beanNamesByNavigationComponents.values()
                    .forEach(names -> names.forEach(this::remove));
            beanNamesByNavigationComponents = activeChain;
        }

        private void putIfNotNull(Map<Class<?>, Set<String>> map, Class<?> key,
                Set<String> value) {
            if (value != null) {
                map.put(key, value);
            }
        }

        private boolean layoutsContainsOwner(RouteScopeOwner owner) {
            return currentLayouts.stream()
                    .anyMatch(clazz -> hasOwnerType(clazz, owner));

        }

        private boolean hasOwnerType(Class<?> clazz, RouteScopeOwner owner) {
            return owner.value().equals(clazz);
        }

        private void addListeners() {
            uiDetachRegistration = currentUI.addDetachListener(this);
            afterNavigationRegistration = currentUI
                    .addAfterNavigationListener(this);
            beforeEnterRegistration = currentUI.addBeforeEnterListener(this);
        }

    }

    @Override
    public void postProcessBeanFactory(
            ConfigurableListableBeanFactory beanFactory) {
        beanFactory.registerScope(VAADIN_ROUTE_SCOPE_NAME, this);
    }

    @Override
    public String getConversationId() {
        return getVaadinSession().getSession().getId() + "-route-scope";
    }

    @Override
    protected BeanStore getBeanStore() {
        final VaadinSession session = getVaadinSession();
        session.lock();
        try {
            RouteStoreWrapper wrapper = session
                    .getAttribute(RouteStoreWrapper.class);
            if (wrapper == null) {
                wrapper = new RouteStoreWrapper(session,
                        getApplicationContext());
                session.setAttribute(RouteStoreWrapper.class, wrapper);
            }
            return wrapper.getBeanStore(getUI());
        } finally {
            session.unlock();
        }
    }

    private static String getWindowName(UI ui) {
        ExtendedClientDetails details = ui.getInternals()
                .getExtendedClientDetails();
        if (details == null) {
            return null;
        }
        return details.getWindowName();
    }

    private static UI getUI() {
        UI ui = UI.getCurrent();
        if (ui == null) {
            throw new IllegalStateException(
                    "There is no UI available. The route scope is not active");
        }
        return ui;
    }
}
