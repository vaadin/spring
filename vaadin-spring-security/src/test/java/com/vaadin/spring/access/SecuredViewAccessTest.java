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
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.WrappedSession;
import com.vaadin.spring.annotation.EnableVaadin;
import com.vaadin.spring.annotation.EnableVaadinNavigation;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.spring.internal.BeanStore;
import com.vaadin.spring.internal.BeanStoreRetrievalStrategy;
import com.vaadin.spring.internal.UIScopeImpl;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test SpringViewProvider access control.
 */
@ContextConfiguration
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
public class SecuredViewAccessTest {

    @Configuration
    @EnableVaadin
    @EnableVaadinNavigation
    public static class Config {
        @Bean
        @UIScope
        public DummyUI ui() {
            return new DummyUI();
        }

        @Bean
        @ViewScope
        public TestViewUnsecured testViewUnsecured() {
            return new TestViewUnsecured();
        }

        @Bean
        @ViewScope
        public TestViewSecured testViewSecured() {
            return new TestViewSecured();
        }

        @Bean
        public ViewAccessControl accessControl() {
            return new SecuredViewAccessControl();
        }

        @Bean
        @ViewScope
        public TestViewAdminOnly testViewAdminOnly() {
            return new TestViewAdminOnly();
        }
    }

    private SpringViewProvider viewProvider;

    @SpringUI
    @SpringViewDisplay
    public static class DummyUI extends UI {
        @Override
        protected void init(VaadinRequest request) {
        }
    }

    public static abstract class DummyView implements View {
        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
        }
    }

    @Autowired
    private WebApplicationContext applicationContext;

    @SpringView(name = TestViewUnsecured.VIEW_NAME, ui = DummyUI.class)
    private static class TestViewUnsecured extends DummyView {
        static final String VIEW_NAME = "unsecured";
    }

    @SpringView(name = TestViewSecured.VIEW_NAME, ui = DummyUI.class)
    @Secured("ROLE_SECURED")
    private static class TestViewSecured extends DummyView {
        static final String VIEW_NAME = "secured";
    }

    @SpringView(name = TestViewAdminOnly.VIEW_NAME, ui = DummyUI.class)
    @Secured("ROLE_ADMIN")
    private static class TestViewAdminOnly extends DummyView {
        static final String VIEW_NAME = "admin";
    }

    @Before
    public void setup() throws ServiceException {
        BeanStore beanStore = new BeanStore("testBeanStore");
        UIScopeImpl.setBeanStoreRetrievalStrategy(
                new BeanStoreRetrievalStrategy() {
                    @Override
                    public BeanStore getBeanStore() {
                        return beanStore;
                    }

                    @Override
                    public String getConversationId() {
                        return "testconversationid";
                    }
                });

        UI ui = new DummyUI();

        VaadinSession session = createVaadinSessionMock();
        CurrentInstance.set(VaadinSession.class, session);
        ui.setSession(session);
        UI.setCurrent(ui);
        viewProvider = applicationContext.getBean(SpringViewProvider.class);
    }

    private VaadinSession createVaadinSessionMock() {
        WrappedSession wrappedSession = mock(WrappedSession.class);
        VaadinService vaadinService = mock(VaadinService.class);

        VaadinSession session = mock(VaadinSession.class);
        when(session.getState()).thenReturn(VaadinSession.State.OPEN);
        when(session.getSession()).thenReturn(wrappedSession);
        when(session.getService()).thenReturn(vaadinService);
        when(session.getSession().getId()).thenReturn("TEST_SESSION_ID");
        when(session.hasLock()).thenReturn(true);
        when(session.getLocale()).thenReturn(Locale.US);
        return session;
    }

    @After
    public void teardownUi() {
        UI.setCurrent(null);
        CurrentInstance.set(VaadinSession.class, null);
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testUnsecuredNoAuth() {
        doTest(TestViewUnsecured.VIEW_NAME);
    }

    @Test
    public void testUnsecuredAuth() {
        setupContext("nobody");
        doTest(TestViewUnsecured.VIEW_NAME);
    }

    private void setupContext(String... authorities) {
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken("p", "c", authorities));
    }

    @Test
    public void testSecuredAuth() {
        setupContext("nobody", "ROLE_SECURED");
        doTest(TestViewUnsecured.VIEW_NAME, TestViewSecured.VIEW_NAME);
    }

    @Test
    public void testAdminAuth() {
        setupContext("ROLE_ADMIN");
        doTest(TestViewUnsecured.VIEW_NAME, TestViewAdminOnly.VIEW_NAME);
    }

    private void doTest(String... allowedViews) {
        Collection<String> viewNamesForCurrentUI = new HashSet<>(viewProvider.getViewNamesForCurrentUI());
        Assert.assertEquals(new HashSet<>(Arrays.asList(allowedViews)), viewNamesForCurrentUI);
    }
}

