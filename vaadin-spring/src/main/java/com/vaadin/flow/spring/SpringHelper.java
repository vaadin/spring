/*
 * Copyright 2000-2017 Vaadin Ltd.
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
package com.vaadin.flow.spring;

import java.util.Iterator;
import java.util.Locale;

import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

/**
 * Utility methods for spring
 */
public class SpringHelper {

    private SpringHelper() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Gets the property name used after being parsed by Spring Boot, accepting
     * relaxed parsing
     *
     * @param env the environment
     * @param propertyName the relaxed property name
     * @return the actual property name
     */
    public static String getActualPropertyName(Environment env, String propertyName) {
        String storedPropertyName = null;
        String normalizedPropertyName = normalizePropertyName(propertyName);
        Iterator it = ((AbstractEnvironment) env).getPropertySources().iterator();
        while(it.hasNext() && storedPropertyName == null) {
            PropertySource propertySource = (PropertySource) it.next();
            if (propertySource instanceof MapPropertySource) {
                storedPropertyName = extractFromPropertySource(
                        propertySource, normalizedPropertyName);
            }
        }
        return storedPropertyName;
    }

    private static String extractFromPropertySource(
            PropertySource propertySource, String normalizedPropertyName) {
        String storedPropertyName = null;
        for (String key : ((MapPropertySource) propertySource)
                .getSource().keySet()) {
            if (normalizePropertyName(key).equals(normalizedPropertyName)) {
                storedPropertyName = key;
                break;
            }
        }
        return storedPropertyName;
    }

    /**
     * Converts a property name to a normalized form, so it can be compared
     *
     * @param propertyName the property name
     * @return the normalized property name
     */
    public static String normalizePropertyName(String propertyName) {
        assert propertyName != null;

        return propertyName.toLowerCase(Locale.ENGLISH)
                .replaceAll("[_\\.-]", "");
    }
}
