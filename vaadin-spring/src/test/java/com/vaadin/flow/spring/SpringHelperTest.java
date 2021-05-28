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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SpringHelperTest {

    @Test
    public void camelCase_normalized_matches() {
        String normalized = SpringHelper
                .normalizePropertyName("vaadin.urlMapping");
        Assert.assertEquals("vaadinurlmapping", normalized);
    }

    @Test
    public void kebabCase_normalized_matches() {
        String normalized = SpringHelper
                .normalizePropertyName("vaadin.url-mapping");
        Assert.assertEquals("vaadinurlmapping", normalized);
    }

    @Test
    public void underscore_normalized_matches() {
        String normalized = SpringHelper
                .normalizePropertyName("vaadin.url_mapping");
        Assert.assertEquals("vaadinurlmapping", normalized);
    }

    @Test
    public void uppercase_normalized_matches() {
        String normalized = SpringHelper
                .normalizePropertyName("vaadin.URL_MAPPING");
        Assert.assertEquals("vaadinurlmapping", normalized);
    }

    @Test
    public void environment_extractRelaxedPropertyName_matches() {
        Environment env = mockEnv("vaadin.url-mapping");
        String actualPropertyName =
                SpringHelper.getActualPropertyName(env,
                        "vaadin.urlMapping");
        Assert.assertEquals("vaadin.url-mapping", actualPropertyName);
    }

    @Test
    public void environment_extractExactPropertyName_matches() {
        Environment env = mockEnv("vaadin.urlMapping");
        String actualPropertyName =
                SpringHelper.getActualPropertyName(env,
                        "vaadin.urlMapping");
        Assert.assertEquals("vaadin.urlMapping", actualPropertyName);
    }

    @Test
    public void environment_extractWrongPropertyName_returnsNull() {
        AbstractEnvironment env = mockEnv("vaadin.urlMapping");
        String actualPropertyName =
                SpringHelper.getActualPropertyName(env,
                        "vaadin.urlwapping");
        Assert.assertNull(actualPropertyName);
    }

    private AbstractEnvironment mockEnv(String containedPropertyName) {
        AbstractEnvironment env = mock(AbstractEnvironment.class);

        MapPropertySource propertySource = mock(MapPropertySource.class);
        Map<String, Object> map = new HashMap<>();
        map.put(containedPropertyName, "");
        when(propertySource.getSource()).thenReturn(map);

        List<PropertySource<?>> list = new ArrayList<>();
        list.add(propertySource);

        MutablePropertySources propertySources = mock(MutablePropertySources.class);
        when(propertySources.iterator()).thenReturn(list.iterator());

        when(env.getPropertySources()).thenReturn(propertySources);

        return env;
    }

}
