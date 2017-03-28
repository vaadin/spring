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
package com.vaadin.spring.boot;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.spring.navigator.SpringNavigator;
import com.vaadin.spring.server.AbstractSpringUIProviderTest;
import com.vaadin.spring.server.SpringVaadinServlet;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
// make sure the context is cleaned
@DirtiesContext
public class VaadinAutoConfigurationTest extends AbstractSpringUIProviderTest {

    @Autowired
    private WebApplicationContext applicationContext;

    @SpringUI
    @SpringViewDisplay
    private static class TestUI extends DummyUI {
    }

    @Configuration
    @EnableAutoConfiguration
    protected static class Config {
        // this gets configured by the UI provider
        @Bean
        public TestUI ui() {
            return new TestUI();
        }
    }

    @Test
    public void testVaadinServletDefined() {
        // this sets up the UI scope
        TestUI ui = createUi(TestUI.class);
        Assert.isInstanceOf(SpringVaadinServlet.class,
                applicationContext.getBean("vaadinServlet"),
                "Vaadin servlet is not autoconfigured");
    }

    @Test
    public void testNavigatorDefined() {
        // this sets up the UI scope
        TestUI ui = createUi(TestUI.class);
        Assert.isInstanceOf(SpringNavigator.class,
                applicationContext.getBean(SpringNavigator.class),
                "Vaadin Navigator is not autoconfigured");
    }

}
