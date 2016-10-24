/*
 * Copyright 2015-2016 The original authors
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
package com.vaadin.spring.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.access.ViewAccessControl;
import com.vaadin.spring.annotation.EnableVaadinNavigation;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewContainer;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.spring.server.AbstractSpringUIProviderTest;
import com.vaadin.ui.UI;

/**
 * Test SpringViewProvider access control.
 */
@ContextConfiguration
@WebAppConfiguration
public class SpringViewProviderAccessControlTest
        extends AbstractSpringUIProviderTest {

    @SpringUI
    @ViewContainer
    private static class TestUI1 extends UI {
        @Override
        protected void init(VaadinRequest request) {
        }
    }

    @SpringUI(path = "other")
    private static class TestUI2 extends UI {
        @Override
        protected void init(VaadinRequest request) {
        }
    }

    @SpringView(name = "")
    private static class TestView1 implements View {
        @Override
        public void enter(ViewChangeEvent event) {
        }
    }

    @SpringView(name = TestView2.VIEW_NAME, ui = TestUI1.class)
    private static class TestView2 implements View {
        static final String VIEW_NAME = "view2";

        @Override
        public void enter(ViewChangeEvent event) {
        }
    }

    @SpringView(name = TestView3.VIEW_NAME, ui = TestUI1.class)
    private static class TestView3 implements View {
        static final String VIEW_NAME = "view3";

        @Override
        public void enter(ViewChangeEvent event) {
        }
    }

    @SpringView(name = TestOtherUiView.VIEW_NAME, ui = TestUI2.class)
    private static class TestOtherUiView implements View {
        static final String VIEW_NAME = "otheruiview";

        @Override
        public void enter(ViewChangeEvent event) {
        }
    }

    protected static class MyViewAccessControl implements ViewAccessControl {
        public Set<String> allowedViewBeans = new HashSet<String>();

        @Override
        public boolean isAccessGranted(UI ui, String beanName) {
            return allowedViewBeans.contains(beanName);
        }
    }

    @Configuration
    @EnableVaadinNavigation
    static class Config extends AbstractSpringUIProviderTest.Config {
        // this gets configured by the UI provider
        @Bean
        public TestUI1 ui1() {
            return new TestUI1();
        }

        @Bean
        public TestUI2 ui2() {
            return new TestUI2();
        }

        // in a real application, these are created dynamically

        @Bean
        @ViewScope
        public TestView1 view1() {
            return new TestView1();
        }

        @Bean
        @ViewScope
        public TestView2 view2() {
            return new TestView2();
        }

        @Bean
        @ViewScope
        public TestView3 view3() {
            return new TestView3();
        }

        @Bean
        @Scope("singleton")
        public MyViewAccessControl accessControl() {
            return new MyViewAccessControl();
        }
    }

    @Autowired
    private WebApplicationContext applicationContext;

    private TestUI1 ui;
    private SpringViewProvider viewProvider;

    @Before
    public void setupUi() {
        // need a UI to set everything up
        ui = createUi(TestUI1.class);
        UI.setCurrent(ui);
        // SpringViewProvider is UI scoped, so needs to be fetched after
        // createUi()
        viewProvider = applicationContext.getBean(SpringViewProvider.class);
    }

    @After
    public void teardownUi() {
        UI.setCurrent(null);
        getAccessControl().allowedViewBeans.clear();
    }

    @Test
    public void testAllowAllViews() throws Exception {
        allowViews("view1", "view2", "view3");
        checkAvailableViews("", TestView2.VIEW_NAME, TestView3.VIEW_NAME);
    }

    @Test
    public void testAllowSomeViews() throws Exception {
        allowViews("view1", "view2");
        checkAvailableViews("", TestView2.VIEW_NAME);
    }

    private void allowViews(String... viewBeanNames) {
        MyViewAccessControl accessControl = getAccessControl();
        for (String viewBeanName : viewBeanNames) {
            accessControl.allowedViewBeans.add(viewBeanName);
        }
    }

    private MyViewAccessControl getAccessControl() {
        return applicationContext.getBean(MyViewAccessControl.class);
    }

    protected void checkAvailableViews(String... viewNames) {
        List<String> views = new ArrayList<String>(
                viewProvider.getViewNamesForCurrentUI());
        Collections.sort(views);
        List<String> expectedViews = new ArrayList<String>(
                Arrays.asList(viewNames));
        Collections.sort(expectedViews);
        Assert.assertEquals("Incorrect set of views returned", expectedViews,
                views);
    }
}
