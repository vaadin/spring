package com.vaadin.flow.spring.form;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.notification.testbench.NotificationElement;
import com.vaadin.flow.component.login.testbench.LoginFormElement;
import com.vaadin.flow.component.login.testbench.LoginOverlayElement;
import com.vaadin.flow.testutil.ChromeBrowserTest;
import com.vaadin.testbench.TestBenchElement;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import org.openqa.selenium.By;

public class FusionFormIT extends ChromeBrowserTest {

    private static final int SERVER_PORT = 9999;

    @Override
    protected int getDeploymentPort() {
        return SERVER_PORT;
    }

    private void open(String path) {
        getDriver().get(getRootURL() + "/" + path);
    }

    @Test
    public void save_empty_values() {
        open("");
        ButtonElement saveButton = $(ButtonElement.class).id("save");
        saveButton.click();
        NotificationElement notification = $(NotificationElement.class).id("notification");
        Assert.assertNotNull(notification);
        waitUntil(driver -> notification.isOpen());
        Assert.assertEquals("saved", notification.getText());
    }

}
