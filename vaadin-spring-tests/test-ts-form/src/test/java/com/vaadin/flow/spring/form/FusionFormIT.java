package com.vaadin.flow.spring.form;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.notification.testbench.NotificationElement;
import com.vaadin.flow.component.textfield.testbench.NumberFieldElement;
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
    public void save_empty_values_for_required_fields_no_runtime_errors() {
        open("");
        ButtonElement saveButton = $(ButtonElement.class).id("save");
        saveButton.click();
        NotificationElement notification = $(NotificationElement.class).id("notification");
        Assert.assertNotNull(notification);
        waitUntil(driver -> notification.isOpen());
        System.out.println(notification.getText());
        Assert.assertTrue(notification.getText().contains("must not be empty"));
        Assert.assertFalse(notification.getText().contains("Expected string but received a undefined"));
    }

    @Test
    // https://github.com/vaadin/fusion/issues/13
    public void no_validation_error_when_clearing_number_field() {
        open("");
        NumberFieldElement numberFieldElement = $(NumberFieldElement.class).id("number-field");
        numberFieldElement.setValue("5");
        blur();
        Assert.assertFalse(numberFieldElement.hasAttribute("invalid"));
        Assert.assertFalse(numberFieldElement.hasAttribute("has-error-message"));
        numberFieldElement.setValue("");
        blur();
        Assert.assertFalse(numberFieldElement.hasAttribute("invalid"));
        Assert.assertFalse(numberFieldElement.hasAttribute("has-error-message"));
    }

}
