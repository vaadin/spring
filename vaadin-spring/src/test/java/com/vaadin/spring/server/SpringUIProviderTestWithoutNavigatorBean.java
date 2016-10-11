package com.vaadin.spring.server;

import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.Assert;

import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.ViewContainer;
import com.vaadin.ui.UI;

@ContextConfiguration
@WebAppConfiguration
public class SpringUIProviderTestWithoutNavigatorBean
        extends AbstractSpringUIProviderTest {

    @SpringUI
    @ViewContainer
    private static class TestUI extends UI {
        @Override
        protected void init(VaadinRequest request) {
        }
    }

    @Configuration
    static class Config extends AbstractSpringUIProviderTest.Config {
        // this gets configured by the UI provider
        @Bean
        public TestUI ui() {
            return new TestUI();
        }
    }

    @Test
    public void testGetNavigator() throws Exception {
        Assert.isNull(getUiProvider().getNavigator(),
                "SpringUIProvider uses a navigator even though none was defined");
    }

    @Test
    public void testConfigureNavigator() {
        TestUI ui = createUi(TestUI.class);
        Assert.isNull(ui.getNavigator(),
                "Navigator should not be configured for UI when no navigator bean is available");
    }
}
