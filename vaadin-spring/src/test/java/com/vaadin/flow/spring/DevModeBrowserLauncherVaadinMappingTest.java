package com.vaadin.flow.spring;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = { "vaadin.urlMapping=/ui/*" })
public class DevModeBrowserLauncherVaadinMappingTest
        extends DevModeBrowserLauncherNoPropertiesTest {

    @Test
    public void foo() {
        String url = DevModeBrowserLauncher.getUrl(app);
        Assert.assertEquals("http://localhost:1244/ui/", url);
    }

}
