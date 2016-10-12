/*
 * Copyright 2015 The original authors
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

import com.vaadin.spring.navigator.SpringNavigator;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
// make sure the context is cleaned
@DirtiesContext
// TODO this test should have an active UI scope
public class VaadinAutoConfigurationWithCustomNavigatorTest {

    @Autowired
    private WebApplicationContext applicationContext;

    private static class MyNavigator extends SpringNavigator {
    }

    @Configuration
    // using this rather than extending a configuration will let us override
    // defaults
    @EnableAutoConfiguration
    protected static class Config {
        @Bean
        public MyNavigator myNavigator() {
            return new MyNavigator();
        }
    }

    @Test
    public void testNavigatorCustomized() {
        Assert.isInstanceOf(MyNavigator.class,
                applicationContext.getBean(SpringNavigator.class),
                "Vaadin Navigator is not correctly overridden");
    }

}
