package com.vaadin.flow.spring.flowsecurity;

import com.vaadin.flow.component.login.testbench.LoginFormElement;
import com.vaadin.flow.component.login.testbench.LoginOverlayElement;
import com.vaadin.flow.testutil.ChromeBrowserTest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class AppViewIT extends ChromeBrowserTest {

    private static final String USER_FULLNAME = "John the User";

    @After
    public void tearDown() {
        if (getDriver() != null) {
            logout();
        }
    }

    private void logout() {
        open("logout");
    }

    private void open(String path) {
        getDriver().get(getRootURL() + "/" + path);
    }

    @Override
    protected void updateHeadlessChromeOptions(ChromeOptions chromeOptions) {
        super.updateHeadlessChromeOptions(chromeOptions);
        chromeOptions.addArguments("--disable-dev-shm-usage");
    }

    @Test
    public void access_restricted_to_admin() {
        String contents = "Secret document for admin";
        String path = "admin-only/secret.txt";
        open(path);
        String anonResult = getDriver().getPageSource();
        Assert.assertFalse(anonResult.contains(contents));
        loginUser();
        open(path);
        String userResult = getDriver().getPageSource();
        Assert.assertFalse(userResult.contains(contents));
        logout();
        open("login");
        loginAdmin();
        open(path);
        String adminResult = getDriver().getPageSource();
        Assert.assertTrue(adminResult.contains(contents));
        logout();
        open(path);
        String anonResult2 = getDriver().getPageSource();
        Assert.assertFalse(anonResult2.contains(contents));
    }

    private void assertPathShown(String path) {
        Assert.assertEquals(getRootURL() + "/" + path, driver.getCurrentUrl());
    }

    private void assertPrivatePageShown(String fullName) {
        assertPathShown("private");
        String balance = $("span").id("balanceText").getText();
        Assert.assertTrue(balance.startsWith("Hello " + fullName + ", your bank account balance is $"));
    }

    private void loginUser() {
        login("john", "john");
    }

    private void loginAdmin() {
        login("emma", "emma");
    }

    private void login(String username, String password) {
        LoginFormElement form = $(LoginOverlayElement.class).first().getLoginForm();
        form.getUsernameField().setValue(username);
        form.getPasswordField().setValue(password);
        form.submit();
        waitUntilNot(driver -> $(LoginOverlayElement.class).exists());
    }

}
