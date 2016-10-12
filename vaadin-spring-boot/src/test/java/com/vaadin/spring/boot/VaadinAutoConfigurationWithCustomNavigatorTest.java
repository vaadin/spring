package com.vaadin.spring.boot;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

import com.vaadin.spring.navigator.SpringNavigator;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
// make sure the context is cleaned
@DirtiesContext
public class VaadinAutoConfigurationWithCustomNavigatorTest {

    @Autowired
    private WebApplicationContext applicationContext;

    private static class MyNavigator extends SpringNavigator {
    }

    @Configuration
    protected static class Config extends VaadinAutoConfiguration {
        @Bean
        public MyNavigator myNavigator() {
            return new MyNavigator();
        }
    }

    @Test
    public void testNavigatorCustomized() {
        Assert.isInstanceOf(MyNavigator.class,
                applicationContext.getBean(SpringNavigator.class),
                "Vaadin Navigator is not correctly overridden");
    }

}
