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
package com.vaadin.spring.web;

import com.gargoylesoftware.htmlunit.WebClient;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Web based test {@link com.vaadin.spring.server.SpringVaadinServlet} static resource handling
 *
 * @author Vaadin Ltd
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestStaticHttp {
    private static final String MANDATORY_BOOTSTRAP_PART = "log('Vaadin bootstrap loaded');";
    @Autowired
    private WebClient webClient;

    @LocalServerPort
    private int port;

    @Test
    public void testExample() throws Exception {
        String javaScriptUrl = "http://localhost:" + port + "/VAADIN/vaadinBootstrap.js?v=8.0-SNAPSHOT";
        String content = this.webClient.getPage(javaScriptUrl).getWebResponse().getContentAsString();
        Assert.assertTrue("Mandatory part of bootstrap is not found",
                content.contains(MANDATORY_BOOTSTRAP_PART));
    }

    @SpringUI
    public static class MyUI extends UI {
        @Override
        protected void init(VaadinRequest vaadinRequest) {
            setContent(
                    new VerticalLayout(
                            new Button("Click me", event -> Notification.show("Thanks"))));
        }
    }

    @SpringBootApplication
    public static class MyConfig {
        @Bean
        public MyUI createUI() {
            return new MyUI();
        }

        @Bean
        public WebClient createWebClient() {
            return new WebClient();
        }
    }
}
