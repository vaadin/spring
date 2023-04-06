/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.spring;

import com.vaadin.fusion.FusionControllerConfiguration;
import com.vaadin.fusion.FusionEndpointProperties;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.context.support.GenericWebApplicationContext;

@SpringBootTest(classes = { FusionEndpointProperties.class })
@ContextConfiguration(classes = { FusionControllerConfiguration.class,
        SpringBootAutoConfiguration.class,
        SpringSecurityAutoConfiguration.class })
@TestPropertySource(properties = { "server.port = 1244" })
public class DevModeBrowserLauncherNoPropertiesTest {

    @Autowired
    protected GenericWebApplicationContext app;

    @Test
    public void getUrl_noProperties_givesUrlWithNoContextPathAndUrlMapping() {
        String url = DevModeBrowserLauncher.getUrl(app);
        Assert.assertEquals("http://localhost:1244/", url);
    }

}
