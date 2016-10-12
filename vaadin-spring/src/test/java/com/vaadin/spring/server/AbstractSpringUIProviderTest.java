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
package com.vaadin.spring.server;

import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import com.vaadin.server.DefaultDeploymentConfiguration;
import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.UICreateEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.WrappedSession;
import com.vaadin.spring.annotation.EnableVaadin;
import com.vaadin.ui.UI;

@RunWith(SpringJUnit4ClassRunner.class)
// make sure the context is cleaned
@DirtiesContext
public abstract class AbstractSpringUIProviderTest {

    private static final class MyVaadinSession extends VaadinSession {

        public MyVaadinSession(MySpringVaadinServletService service) {
            super(service);
        }

        @Override
        public boolean hasLock() {
            return true;
        }

    }

    // override methods to increase their visibility
    private static final class MySpringVaadinServletService
            extends SpringVaadinServletService {
        private Lock sessionLock = new ReentrantLock();

        private MySpringVaadinServletService(VaadinServlet servlet)
                throws ServiceException {
            super(servlet, new DefaultDeploymentConfiguration(
                    MySpringVaadinServletService.class, new Properties()), "");
            init();
        }

        @Override
        public MyVaadinSession createVaadinSession(VaadinRequest request)
                throws com.vaadin.server.ServiceException {
            return new MyVaadinSession(this);
        }

        @Override
        protected Lock getSessionLock(WrappedSession wrappedSession) {
            return sessionLock;
        }

        @Override
        public void lockSession(WrappedSession wrappedSession) {
            super.lockSession(wrappedSession);
        }
    }

    private static final class MyVaadinServlet extends SpringVaadinServlet {
        @Override
        public MySpringVaadinServletService getService() {
            return (MySpringVaadinServletService) super.getService();
        }

        @Override
        protected MySpringVaadinServletService createServletService(
                DeploymentConfiguration deploymentConfiguration)
                throws ServiceException {
            // this is needed when using a custom service URL
            MySpringVaadinServletService service = new MySpringVaadinServletService(
                    this);
            service.init();
            return service;
        }
    }

    @Configuration
    @EnableVaadin
    protected static class Config {
        @Bean
        public MyVaadinServlet vaadinServlet() {
            return new MyVaadinServlet();
        }
    }

    protected static final int TEST_UIID = 123;

    @Autowired
    private WebApplicationContext applicationContext;

    @Autowired
    private MockServletContext servletContext;

    @Autowired
    private MockHttpServletRequest request;

    @Autowired
    private MockHttpSession session;

    @Autowired
    private MyVaadinServlet servlet;

    private MySpringVaadinServletService service;
    private SpringVaadinServletRequest vaadinServletRequest;
    private MyVaadinSession vaadinSession;
    private SpringUIProvider uiProvider;

    @Before
    public void setup() throws Exception {
        // need to circumvent a lot of normal mechanisms as many relevant
        // methods are private
        // TODO very ugly - can this be simplified?
        servlet.init(new MockServletConfig(servletContext));
        service = servlet.getService();
        vaadinServletRequest = new SpringVaadinServletRequest(request, service,
                true);
        vaadinSession = service.createVaadinSession(vaadinServletRequest);
        VaadinSession.setCurrent(vaadinSession);
        // make sure the lock etc. exist in the session
        ReflectionTestUtils.setField(vaadinSession, "service", service);
        ReflectionTestUtils.setField(vaadinSession, "session",
                vaadinServletRequest.getWrappedSession());
        ReflectionTestUtils.setField(vaadinSession, "lock",
                service.sessionLock);

        service.lockSession(vaadinSession.getSession());

        uiProvider = new SpringUIProvider(getVaadinSession());
    }

    @After
    public void tearDown() {
        vaadinSession.unlock();
        VaadinSession.setCurrent(null);
    }

    protected SpringUIProvider getUiProvider() {
        return uiProvider;
    }

    protected UICreateEvent buildUiCreateEvent(Class<? extends UI> uiClass) {
        return new UICreateEvent(getVaadinServletRequest(), uiClass, TEST_UIID);
    }

    @SuppressWarnings("unchecked")
    protected <T extends UI> T createUi(Class<T> uiClass) {
        return (T) getUiProvider().createInstance(buildUiCreateEvent(uiClass));
    }

    public MySpringVaadinServletService getService() {
        return service;
    }

    public SpringVaadinServletRequest getVaadinServletRequest() {
        return vaadinServletRequest;
    }

    public VaadinSession getVaadinSession() {
        return vaadinSession;
    }

}
