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

    public static void startSpringApplication(Class<?> appClass, String[] args) {
        initBowerMode();
        SpringApplication.run(appClass, args);
    }

    public static void initBowerMode() {
        if (System.getProperty("bowerMode") != null) {
            System.setProperty("vaadin.compatibilityMode", "true");
            System.out.println("Compatibility mode has been set");
        }
    }
}
