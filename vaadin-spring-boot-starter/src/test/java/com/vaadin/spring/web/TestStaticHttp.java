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
package com.vaadin.spring.web;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;

/**
 * Web based test {@link com.vaadin.spring.server.SpringVaadinServlet} static resource handling
 *
 * @author Vaadin Ltd
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestStaticHttp {

    private static final String MANDATORY_BOOTSTRAP_PART = "log('Vaadin bootstrap loaded');";

    @LocalServerPort
    private int port;

    @Test
    public void testExample() throws Exception {
        String javaScriptUrl = "http://localhost:" + port + "/VAADIN/vaadinBootstrap.js";
        HttpRequest request = HttpRequest.newBuilder(URI.create(javaScriptUrl)).build();
        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode(),
                "Unexpected status for " + javaScriptUrl);
        Assertions.assertTrue(response.body().contains(MANDATORY_BOOTSTRAP_PART),
                "Mandatory part of bootstrap is not found");
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
    }
}
