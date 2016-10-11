package com.vaadin.spring.server;

import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.Assert;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.ViewContainer;
import com.vaadin.spring.navigator.SpringNavigator;
import com.vaadin.ui.UI;

/**
 * Test for normal (full) use cases of SpringUIProvider with automatic
 * navigation configuration on the view.
 */
@ContextConfiguration
@WebAppConfiguration
public class SpringUIProviderTestWithUiImplementingViewDisplayAsViewContainer
        extends AbstractSpringUIProviderTest {

    @SpringUI
    @ViewContainer
    private static class TestUI extends UI implements ViewDisplay {
        @Override
        protected void init(VaadinRequest request) {
        }

        @Override
        public void showView(View view) {
        }
    }

    @Configuration
    static class Config extends AbstractSpringUIProviderTest.Config {
        @Bean
        public SpringNavigator navigator() {
            return new SpringNavigator();
        }

        // this gets configured by the UI provider
        @Bean
        public TestUI ui() {
            return new TestUI();
        }
    }

    @Test
    public void testGetNavigator() throws Exception {
        Assert.notNull(getUiProvider().getNavigator(),
                "Navigator not available in SpringUIProvider");
    }

    @Test
    public void testConfigureNavigator() {
        TestUI ui = createUi(TestUI.class);
        Assert.isTrue(ui.getNavigator().getDisplay() instanceof TestUI,
                "Navigator is not configured for a custom ViewDisplay");
    }

    @Test
    public void testFindViewContainer() throws Exception {
        TestUI ui = createUi(TestUI.class);
        Assert.isInstanceOf(TestUI.class, getUiProvider().findViewContainer(ui),
                "View container is not a TestUI");
    }

}
