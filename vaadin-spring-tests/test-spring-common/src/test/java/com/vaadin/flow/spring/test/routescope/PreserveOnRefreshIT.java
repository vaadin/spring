package com.vaadin.flow.spring.test.routescope;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.flow.spring.test.AbstractSpringTest;

public class PreserveOnRefreshIT extends AbstractSpringTest {

    @Override
    protected String getTestPath() {
        return "/preserve-on-refresh";
    }

    @Test
    public void routeScopedBeanIsPreservedAfterViewRefresh() {
        open();

        String beanCall = findElement(By.id("preserve-on-refresh")).getText();

        // refresh
        open();

        Assert.assertEquals("Bean is not preserved after refresh", beanCall,
                findElement(By.id("preserve-on-refresh")).getText());
    }
}
