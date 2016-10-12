package com.vaadin.spring.boot;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

import com.vaadin.spring.navigator.SpringNavigator;
import com.vaadin.spring.server.SpringVaadinServlet;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
// make sure the context is cleaned
@DirtiesContext
public class VaadinAutoConfigurationTest {

    @Autowired
    private WebApplicationContext applicationContext;

    @Configuration
    protected static class Config extends VaadinAutoConfiguration {
    }

    @Test
    public void testVaadinServletDefined() {
        Assert.isInstanceOf(SpringVaadinServlet.class,
                applicationContext.getBean("vaadinServlet"),
                "Vaadin servlet is not autoconfigured");
    }

    @Test
    public void testNavigatorDefined() {
        Assert.isInstanceOf(SpringNavigator.class,
                applicationContext.getBean(SpringNavigator.class),
                "Vaadin Navigator is not autoconfigured");
    }

}
