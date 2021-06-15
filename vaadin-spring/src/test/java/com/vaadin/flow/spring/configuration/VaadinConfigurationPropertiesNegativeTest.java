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
        "vaadin.production-mode=true",
        "vaadin.enable-dev-server=false",
        "vaadin.pnpm.enable=false",
        "vaadin.disable-xsrf-protection=true",
}, classes = { VaadinConfigurationProperties.class })
public class VaadinConfigurationPropertiesNegativeTest {

    @Autowired
    private ApplicationContext context;

    @Test
    public void hasConfiguration_productionMode_servletSet() throws ServletException {
        VaadinService service = SpringInstantiatorTest.getService(context,
                new Properties());
        boolean productionMode = service.getDeploymentConfiguration()
                .isProductionMode();
        Assert.assertEquals(true, productionMode);
    }

    @Test
    public void hasConfiguration_enableDevServer_servletSet() throws ServletException {
        VaadinService service = SpringInstantiatorTest.getService(context,
                new Properties());
        boolean enableDevServer = service.getDeploymentConfiguration()
                .enableDevServer();
        Assert.assertEquals(false, enableDevServer);
    }

    @Test
    public void hasConfiguration_pnpmEnabled_servletSet() throws ServletException {
        VaadinService service = SpringInstantiatorTest.getService(context,
                new Properties());
        boolean pnpmEnabled = service.getDeploymentConfiguration()
                .isPnpmEnabled();
        Assert.assertEquals(false, pnpmEnabled);
    }


    @Test
    public void hasConfiguration_xsrfProtectionEnabled_servletSet() throws ServletException {
        VaadinService service = SpringInstantiatorTest.getService(context,
                new Properties());
        boolean xsrfProtectionEnabled = service.getDeploymentConfiguration()
                .isXsrfProtectionEnabled();
        Assert.assertEquals(false, xsrfProtectionEnabled);
    }
}