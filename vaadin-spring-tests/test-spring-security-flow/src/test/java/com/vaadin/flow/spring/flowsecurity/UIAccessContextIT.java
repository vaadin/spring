package com.vaadin.flow.spring.flowsecurity;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.login.testbench.LoginFormElement;
import com.vaadin.flow.component.login.testbench.LoginOverlayElement;
import com.vaadin.testbench.HasElementQuery;
import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.TestBenchElement;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class UIAccessContextIT extends AbstractIT {

    private WebDriver createHeadlessChromeDriver() {
        ChromeOptions headlessOptions = new ChromeOptions();
        headlessOptions.addArguments("--headless", "--disable-gpu");
        return TestBench.createDriver(new ChromeDriver(headlessOptions));
    }

    @Test
    public void securityContextSetForUIAccess() {
        String expectedUserBalance = "Hello John the User, your bank account balance is $10000.00.";
        String expectedAdminBalance = "Hello Emma the Admin, your bank account balance is $200000.00.";

        open("private");
        loginUser();
        TestBenchElement balance = $("span").id("balanceText");
        Assert.assertEquals(expectedUserBalance, balance.getText());

        WebDriver adminBrowser = createHeadlessChromeDriver();
        open("private", adminBrowser);
        HasElementQuery adminContext = new HasElementQuery() {

            @Override
            public SearchContext getContext() {
                return adminBrowser;
            }

        };
        loginAdmin(adminContext);
        TestBenchElement adminBalance = adminContext.$("span")
                .id("balanceText");
        Assert.assertEquals(expectedAdminBalance, adminBalance.getText());

        ButtonElement sendRefresh = $(ButtonElement.class).id("sendRefresh");
        sendRefresh.click();
        Assert.assertEquals(expectedUserBalance, balance.getText());
        Assert.assertEquals(expectedAdminBalance, adminBalance.getText());

        ButtonElement adminSendRefresh = adminContext.$(ButtonElement.class)
                .id("sendRefresh");
        adminSendRefresh.click();
        Assert.assertEquals(expectedUserBalance, balance.getText());
        Assert.assertEquals(expectedAdminBalance, adminBalance.getText());
    }

    private void loginAdmin(HasElementQuery adminContext) {
        LoginFormElement form = adminContext.$(LoginOverlayElement.class)
                .first().getLoginForm();
        form.getUsernameField().setValue("emma");
        form.getPasswordField().setValue("emma");
        form.submit();
    }

}
