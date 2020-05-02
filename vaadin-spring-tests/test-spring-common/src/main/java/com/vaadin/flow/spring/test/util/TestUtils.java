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

import java.util.Properties;

/**
 * Shared code to use in the Spring integration tests.
 */
public final class TestUtils {

    private TestUtils() {
    }

    /**
     * Collects current system properties with Vaadin common system properties
     * in {@link Properties} object.
     *
     * Workaround for https://github.com/vaadin/spring/issues/605
     *
     * @return current system properties plus common Vaadin system properties
     */
    public static Properties getVaadinProperties() {
        Properties properties = System.getProperties();
        properties.setProperty("vaadin.compatibilityMode", "");
        return properties;
    }

    /**
     * Sets Vaadin common system properties.
     */
    public static void setVaadinProperties() {
        System.setProperties(getVaadinProperties());
    }
}
