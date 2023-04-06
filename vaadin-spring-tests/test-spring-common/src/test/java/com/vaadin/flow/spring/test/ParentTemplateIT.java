package com.vaadin.flow.spring.test;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.TestBenchElement;

public class ParentTemplateIT extends AbstractSpringTest {

    @Override
    protected String getTestPath() {
        return "/parent-template";
    }

    @Test
    public void customElementIsRegistered() throws Exception {
        open();

        TestBenchElement template = $("parent-template").first();
        TestBenchElement div = template.$("*").id("div");

        Assert.assertEquals("baz", div.getText());

        TestBenchElement child = template.$("*").id("child");

        Assert.assertEquals("bar", child.$("*").id("info").getText());
    }

    @Test
    public void injectedComponentIsSpringManaged() throws Exception {
        open();

        TestBenchElement template = $("parent-template").first();

        TestBenchElement child = template.$("*").id("child");

        Assert.assertEquals("foo", child.$("*").id("message").getText());
    }
}
