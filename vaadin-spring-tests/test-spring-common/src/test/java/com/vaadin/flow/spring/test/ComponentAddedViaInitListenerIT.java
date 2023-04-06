package com.vaadin.flow.spring.test;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.TestBenchElement;

public class ComponentAddedViaInitListenerIT extends AbstractSpringTest {

    @Test
    public void componentAddedViaInitListenerIsLoaded() {
        open();

        TestBenchElement component = $("init-listener-component").first();
        TestBenchElement div = component.$("div").first();
        Assert.assertEquals("Init Listener Component", div.getText());
    }

    @Override
    protected String getTestPath() {
        return "/";
    }
}
