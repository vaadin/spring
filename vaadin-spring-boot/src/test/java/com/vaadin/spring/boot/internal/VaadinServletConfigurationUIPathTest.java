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
package com.vaadin.spring.boot.internal;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

import com.vaadin.navigator.PushStateNavigation;
import com.vaadin.spring.annotation.EnableVaadinNavigation;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.server.AbstractSpringUIProviderTest;
import com.vaadin.spring.server.AbstractSpringUIProviderTest.DummyUI;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
// make sure the context is cleaned
@DirtiesContext
public class VaadinServletConfigurationUIPathTest {

    @SpringUI
    private static class Root extends DummyUI {
    }

    @SpringUI(path = "sub/*")
    private static class Sub extends DummyUI {
    }

    @SpringUI(path = "wild/**")
    private static class Wildcard extends DummyUI {
    }

    @SpringUI(path = "pushState")
    @PushStateNavigation
    private static class PushState extends DummyUI {
    }

    private static class MyVaadinServletConfiguration
            extends VaadinServletConfiguration {
    }

    private static class MyVaadinServletConfigurationProperties
            extends VaadinServletConfigurationProperties {
    }

    @Configuration
    @EnableVaadinNavigation
    static class Config extends AbstractSpringUIProviderTest.Config {
        @Bean
        public Root root() {
            return new Root();
        }

        @Bean
        public Sub sub() {
            return new Sub();
        }

        @Bean
        public Wildcard wildcard() {
            return new Wildcard();
        }

        @Bean
        public PushState pushState() {
            return new PushState();
        }

        @Bean
        public MyVaadinServletConfiguration myVaadinServletConfiguration() {
            return new MyVaadinServletConfiguration();
        }

        @Bean
        public MyVaadinServletConfigurationProperties myVaadinServletConfigurationProperties() {
            return new MyVaadinServletConfigurationProperties();
        }
    }

    @Autowired
    VaadinServletConfiguration configuration;

    @Test
    public void testUIMappings() {
        SimpleUrlHandlerMapping mapping = configuration
                .vaadinUiForwardingHandlerMapping();

        Set<String> keySet = new HashSet<>(mapping.getUrlMap().keySet());

        Stream.of("/", "/sub", "/sub/*", "/wild", "/wild/**", "/pushState",
                "/pushState/**").forEach(mappedPath -> {
                    assertTrue("Expected mapping not found: " + mappedPath,
                            keySet.remove(mappedPath));
                });

        assertTrue(
                "Extra path mapped: "
                        + keySet.stream().collect(Collectors.joining(", ")),
                keySet.isEmpty());
    }
}
