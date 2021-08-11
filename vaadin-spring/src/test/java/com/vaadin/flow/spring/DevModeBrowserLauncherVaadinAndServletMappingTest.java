package com.vaadin.flow.spring;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = { "vaadin.urlMapping=/ui/*" })
public class DevModeBrowserLauncherVaadinAndServletMappingTest
        extends DevModeBrowserLauncherNoPropertiesTest {

    @Test
    public void foo() {
        MockServletContext ctx = (MockServletContext) app.getServletContext();
        ctx.setContextPath("/contextpath");
        String url = DevModeBrowserLauncher.getUrl(app);
        Assert.assertEquals("http://localhost:1244/contextpath/ui/", url);
    }

}
