package com.vaadin.spring.server;

import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.Assert;

import com.vaadin.navigator.Navigator.SingleComponentContainerViewDisplay;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.spring.annotation.ViewContainer;
import com.vaadin.spring.navigator.SpringNavigator;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;

/**
 * Test for normal (full) use cases of SpringUIProvider with automatic
 * navigation configuration on the view.
 */
@ContextConfiguration
@WebAppConfiguration
public class SpringUIProviderTestWithPanelAsViewContainer
        extends AbstractSpringUIProviderTest {

    @SpringUI
    private static class TestUI extends UI {
        @Override
        protected void init(VaadinRequest request) {
        }
    }

    @UIScope
    @Component
    @ViewContainer
    private static class MyPanel extends Panel {
    }

    @Configuration
    static class Config extends AbstractSpringUIProviderTest.Config {
        @Bean
        public SpringNavigator navigator() {
            return new SpringNavigator();
        }

        @Bean
        public MyPanel myPanel() {
            return new MyPanel();
        }

        // this gets configured by the UI provider
        @Bean
        public TestUI ui() {
            return new TestUI();
        }
    }

    @Test
    public void testConfigureNavigator() {
        TestUI ui = createUi(TestUI.class);
        Assert.isInstanceOf(SingleComponentContainerViewDisplay.class,
                ui.getNavigator().getDisplay(),
                "Navigator is not configured for SingleComponentContainerViewDisplay");
    }

    @Test
    public void testFindViewContainer() throws Exception {
        TestUI ui = createUi(TestUI.class);
        Assert.isInstanceOf(MyPanel.class,
                getUiProvider().findViewContainer(ui),
                "View container is not a Panel");
    }

}
