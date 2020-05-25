/*
 * Copyright 2000-2020 Vaadin Ltd.
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
package com.vaadin.flow.spring.test.util;

import org.springframework.boot.SpringApplication;

/**
 * Shared code to use in the Spring integration tests.
 */
public final class TestUtils {

    private TestUtils() {
    }

    /**
     * Sets Vaadin system properties and starts the Spring-boot application.
     * @param appClass spring-boot application main class
     * @param args java command-line arguments
     */
    public static void startSpringApplication(Class<?> appClass, String[] args) {
        setVaadinProperties();
        SpringApplication.run(appClass, args);
    }

    /**
     * Sets an essential Vaadin system properties.
     */
    public static void setVaadinProperties() {
        // Sets compatibility mode if application is launched with bower profile.
        // Workaround for https://github.com/vaadin/spring/issues/605
        System.setProperty("vaadin.compatibilityMode", String.valueOf(System.getProperty("bowerMode") != null));
    }
}
