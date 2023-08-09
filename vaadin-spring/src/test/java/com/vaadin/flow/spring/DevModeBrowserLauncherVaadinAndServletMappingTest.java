/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.spring;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = { "vaadin.urlMapping=/ui/*" })
public class DevModeBrowserLauncherVaadinAndServletMappingTest
        extends DevModeBrowserLauncherNoPropertiesTest {

    @Test
    public void getUrl_withContextPathAndUrlMapping_givesUrlWithContextPathAndUrlMapping() {
        MockServletContext ctx = (MockServletContext) app.getServletContext();
        ctx.setContextPath("/contextpath");
        String url = DevModeBrowserLauncher.getUrl(app);
        Assert.assertEquals("http://localhost:1244/contextpath/ui/", url);
    }

}