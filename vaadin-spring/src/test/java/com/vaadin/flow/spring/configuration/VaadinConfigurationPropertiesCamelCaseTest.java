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
package com.vaadin.flow.spring.configuration;

import javax.servlet.ServletException;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.spring.VaadinConfigurationProperties;
import com.vaadin.flow.spring.instantiator.SpringInstantiatorTest;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
        "vaadin.urlMapping=/xxx",
        "vaadin.productionMode=false",
        "vaadin.enableDevServer=true",
        "vaadin.pnpm.enable=true",
        "vaadin.heartbeatInterval=500",
        "vaadin.closeIdleSessions=true",
        "vaadin.devmode.liveReload.enabled=true",
        "vaadin.disableXsrfProtection=true",
        "vaadin.eagerServerLoad=true",
        "vaadin.maxMessageSuspendTimeout=600",
        "vaadin.pushUrl=AAA",
        "vaadin.requestTiming=true",
        "vaadin.syncIdCheck=true",
        "vaadin.useDeprecatedV14Bootstrapping=true",
        "vaadin.sendUrlsAsParameters=true"
}, classes = { VaadinConfigurationProperties.class })
public class VaadinConfigurationPropertiesCamelCaseTest extends VaadinConfigurationPropertiesTest {

}
