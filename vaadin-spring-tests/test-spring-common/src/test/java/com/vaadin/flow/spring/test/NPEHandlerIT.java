package com.vaadin.flow.spring.test;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

public class NPEHandlerIT extends AbstractSpringTest {

    @Override
    protected String getTestPath() {
        return "/npe";
    }

    @Test
    public void npeIsHandledByComponent() {
        open();

        Assert.assertTrue(
                "Couldn't find element from the component which represents error for "
                        + NullPointerException.class.getName(),
                isElementPresent(By.id("npe-handle")));
    }

    @Test
    public void noRouteIsHandledByExistingFlowComponent() {
        String nonExistingRoutePath = "non-existing-route";
        getDriver().get(getTestURL(getRootURL(),
                getContextPath() + '/' + nonExistingRoutePath, new String[0]));

        Assert.assertTrue(getDriver().getPageSource().contains(String
                .format("Could not navigate to '%s'", nonExistingRoutePath)));
    }
}
