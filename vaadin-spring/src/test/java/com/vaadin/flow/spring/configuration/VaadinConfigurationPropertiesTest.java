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
        "vaadin.url-mapping=/xxx",
        "vaadin.production-mode=false",
        "vaadin.enable-dev-server=true",
        "vaadin.pnpm.enable=true",
        "vaadin.heartbeat-interval=500",
        "vaadin.close-idle-sessions=true",
        "vaadin.devmode.live-reload.enabled=true",
        "vaadin.disable-xsrf-protection=true",
        "vaadin.eager-server-load=true",
        "vaadin.max-message-suspend-timeout=600",
        "vaadin.push-url=AAA",
        "vaadin.request-timing=true",
        "vaadin.sync-id-check=true",
        "vaadin.use-deprecated-v14-bootstrapping=true",
        "vaadin.send-urls-as-parameters=true"
}, classes = { VaadinConfigurationProperties.class })
public class VaadinConfigurationPropertiesTest {

    @Autowired
    private ApplicationContext context;

    @Test
    public void hasConfiguration_productionMode_servletSet() throws ServletException {
        VaadinService service = SpringInstantiatorTest.getService(context,
                new Properties());
        boolean productionMode = service.getDeploymentConfiguration()
                .isProductionMode();
        Assert.assertEquals(false, productionMode);
    }

    @Test
    public void hasConfiguration_pnpmEnabled_servletSet() throws ServletException {
        VaadinService service = SpringInstantiatorTest.getService(context,
                new Properties());
        boolean pnpmEnabled = service.getDeploymentConfiguration()
                .isPnpmEnabled();
        Assert.assertEquals(true, pnpmEnabled);
    }

    @Test
    public void hasConfiguration_heartbeatInterval_servletSet() throws ServletException {
        VaadinService service = SpringInstantiatorTest.getService(context,
                new Properties());
        int heartbetInterval = service.getDeploymentConfiguration()
                .getHeartbeatInterval();
        Assert.assertEquals(500, heartbetInterval);
    }

    @Test
    public void hasConfiguration_closeIdelSessions_servletSet() throws ServletException {
        VaadinService service = SpringInstantiatorTest.getService(context,
                new Properties());
        boolean closeIdleSessions = service.getDeploymentConfiguration()
                .isCloseIdleSessions();
        Assert.assertEquals(true, closeIdleSessions);
    }

    @Test
    public void hasConfiguration_devModeLiveReloadEnabled_servletSet() throws ServletException {
        VaadinService service = SpringInstantiatorTest.getService(context,
                new Properties());
        // live reload needs production mode set to false and devmode enabled
        /*
                return !isProductionMode()
                && getBooleanProperty(
                        SERVLET_PARAMETER_DEVMODE_ENABLE_LIVE_RELOAD, true)
                && enableDevServer(); // gizmo excluded from prod bundle
         */
        boolean devModeLiveReloadEnabled = service.getDeploymentConfiguration()
                .isDevModeLiveReloadEnabled();
        Assert.assertEquals(true, devModeLiveReloadEnabled);
    }

    @Test
    public void hasConfiguration_xsrfProtectionEnabled_servletSet() throws ServletException {
        VaadinService service = SpringInstantiatorTest.getService(context,
                new Properties());
        boolean xsrfProtectionEnabled = service.getDeploymentConfiguration()
                .isXsrfProtectionEnabled();
        Assert.assertEquals(false, xsrfProtectionEnabled);
    }

    @Test
    public void hasConfiguration_eagerServerLoad_servletSet() throws ServletException {
        VaadinService service = SpringInstantiatorTest.getService(context,
                new Properties());
        boolean eagerServerLoad = service.getDeploymentConfiguration()
                .isEagerServerLoad();
        Assert.assertEquals(true, eagerServerLoad);
    }

    @Test
    public void hasConfiguration_requestTiming_servletSet() throws ServletException {
        VaadinService service = SpringInstantiatorTest.getService(context,
                new Properties());
        boolean requestTiming = service.getDeploymentConfiguration()
                .isRequestTiming();
        Assert.assertEquals(true, requestTiming);
    }

    @Test
    public void hasConfiguration_syncIdCheckEnabled_servletSet() throws ServletException {
        VaadinService service = SpringInstantiatorTest.getService(context,
                new Properties());
        boolean syncIdCheckEnabled = service.getDeploymentConfiguration()
                .isSyncIdCheckEnabled();
        Assert.assertEquals(true, syncIdCheckEnabled);
    }

    @Test
    public void hasConfiguration_useV14Bootstrap_servletSet() throws ServletException {
        VaadinService service = SpringInstantiatorTest.getService(context,
                new Properties());
        boolean useV14Bootstrap = service.getDeploymentConfiguration()
                .useV14Bootstrap();
        Assert.assertEquals(true, useV14Bootstrap);
    }

    @Test
    public void hasConfiguration_sendUrlsAsParameters_servletSet() throws ServletException {
        VaadinService service = SpringInstantiatorTest.getService(context,
                new Properties());
        boolean sendUrlsAsParameters = service.getDeploymentConfiguration()
                .isSendUrlsAsParameters();
        Assert.assertEquals(true, sendUrlsAsParameters);
    }

    @Test
    public void hasConfiguration_maxMessageSuspendTimeout_servletSet() throws ServletException {
        VaadinService service = SpringInstantiatorTest.getService(context,
                new Properties());
        int maxMessageSuspendTimeout = service.getDeploymentConfiguration()
                .getMaxMessageSuspendTimeout();
        Assert.assertEquals(600, maxMessageSuspendTimeout);
    }

    @Test
    public void hasConfiguration_pushURL_servletSet() throws ServletException {
        VaadinService service = SpringInstantiatorTest.getService(context,
                new Properties());
        String pushURL = service.getDeploymentConfiguration().getPushURL();
        Assert.assertEquals("AAA", pushURL);
    }

}
