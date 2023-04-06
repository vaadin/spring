package com.vaadin.flow.spring.test;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class CoExistingSpringEndpointsIT extends AbstractSpringTest {

    @Override
    protected String getTestPath() {
        return "/";
    }

    @Test
    public void assertRoutesAndSpringEndpoint() {
        open();

        String nonExistingRoutePath = "non-existing-route";
        Assert.assertTrue(isElementPresent(By.id("main")));

        getDriver().get(getContextRootURL() + '/' + nonExistingRoutePath);

        Assert.assertTrue(getDriver().getPageSource().contains(String
                .format("Could not navigate to '%s'", nonExistingRoutePath)));

        getDriver().get(getContextRootURL() + "/oauth/authorize");

        WebElement header = findElement(By.tagName("h1"));
        Assert.assertEquals("OAuth Error", header.getText());
    }
}
