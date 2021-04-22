package com.vaadin.flow.spring.fusionsecurity;

import com.vaadin.flow.component.login.testbench.LoginFormElement;
import com.vaadin.flow.component.login.testbench.LoginOverlayElement;
import com.vaadin.flow.testutil.ChromeBrowserTest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class AppViewIT extends ChromeBrowserTest {

    @Override
    protected int getDeploymentPort() {
        return 9999;
    }

    @After
    public void tearDown() {
        if (getDriver() != null) {
            logout();
        }
    }

    private void logout() {
        open("logout");
    }

    private void clickLogout() {
        navigateTo("logout", false);
    }

    private void open(String path) {
        getDriver().get(getRootURL() + "/" + path);
    }

    @Test
    public void root_page_does_not_require_login() {
        open("");
        assertRootPageShown();
        checkLogsForErrors();
    }

    @Test
    public void navigate_to_private_view_prevented() {
        open("");
        navigateTo("private", false);
        assertLoginViewShown();
    }

    private void assertLoginViewShown() {
        assertPathShown("login");
        waitUntil(driver -> $(LoginOverlayElement.class).exists());
    }

    private void navigateTo(String path) {
        navigateTo(path, true);
    }

    private void navigateTo(String path, boolean assertPathShown) {
        $("main-view").first().$("a").attribute("href", path).first().click();
        if (assertPathShown) {
            assertPathShown(path);
        }
    }

    private void assertPathShown(String path) {
        Assert.assertEquals(getRootURL() + "/" + path, driver.getCurrentUrl());
    }

    @Test
    public void private_page_should_require_login() {
        open("private");
        waitForPath("login");
        loginUser();
        waitForPath("private");
        assertPrivatePageShown("John the User");
        checkLogsForErrors();
    }

    @Test
    public void private_page_logout_should_redirect_to_root() {
        open("login");
        loginUser();
        navigateTo("private");
        clickLogout();
        assertRootPageShown();
        checkLogsForErrors();
    }

    private void assertRootPageShown() {
        Assert.assertEquals("Welcome to the TypeScript Bank of Vaadin", $("h1").id("header").getText());
    }

    private void assertPrivatePageShown(String fullName) {
        String balance = $("span").id("balanceText").getText();
        Assert.assertTrue(balance.startsWith("Hello " + fullName + ", your bank account balance is $"));
    }

    @Test
    public void refresh_when_logged_in_stays_logged_in() {
        open("private");
        loginUser();
        assertPrivatePageShown("John the User");
        refresh();
        assertPrivatePageShown("John the User");
        checkLogsForErrors();
    }

    private void refresh() {
        getDriver().navigate().refresh();
    }

    private void waitForPath(String path) {
        waitUntil(ExpectedConditions.urlToBe(getRootURL() + "/" + path));
    }

    @Test
    public void access_restricted_to_logged_in_users() {
        String contents = "Secret document for all logged in users";
        String path = "all-logged-in/secret.txt";

        open(path);
        String anonResult = getDriver().getPageSource();
        Assert.assertFalse(anonResult.contains(contents));
        loginUser();
        open(path);
        String userResult = getDriver().getPageSource();
        Assert.assertTrue(userResult.contains(contents));
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

    @Test
    public void static_resources_accessible_without_login() throws Exception {
        open("manifest.webmanifest");
        Assert.assertTrue(getDriver().getPageSource().contains("\"name\":\"Spring Security Helper Test Project\""));
        open("sw.js");
        Assert.assertTrue(getDriver().getPageSource().contains("this._installAndActiveListenersAdded"));
        open("sw-runtime-resources-precache.js");
        Assert.assertTrue(getDriver().getPageSource().contains("self.additionalManifestEntries = ["));
    }

    @Test
    public void public_app_resources_available_for_all() {
        open("public/public.txt");
        String shouldBeTextFile = getDriver().getPageSource();
        Assert.assertTrue(shouldBeTextFile.contains("Public document for all users"));
        open("login");
        loginUser();
        open("public/public.txt");
        shouldBeTextFile = getDriver().getPageSource();
        Assert.assertTrue(shouldBeTextFile.contains("Public document for all users"));
    }

    private void loginUser() {
        login("john", "john");
    }

    private void loginAdmin() {
        login("emma", "emma");
    }

    private void login(String username, String password) {
        assertLoginViewShown();

        LoginFormElement form = $(LoginOverlayElement.class).first().getLoginForm();
        form.getUsernameField().setValue(username);
        form.getPasswordField().setValue(password);
        form.submit();
        waitUntilNot(driver -> $(LoginOverlayElement.class).exists());
    }

}
