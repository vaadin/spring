package com.vaadin.flow.spring.test;

import java.io.IOException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.vaadin.flow.testutil.ChromeBrowserTest;

public class AppViewIT extends ChromeBrowserTest {

    @After
    public void tearDown() {
        getDriver().get(getRootURL() + "/logout");
    }

    @Test
    public void root_page_should_load() {
        // when the root is opened
        getDriver().get(getRootURL());

        // then it contains an element and there are no client errors
        Assert.assertNotNull(findElement(By.id("root")));
        checkLogsForErrors();
    }

    @Test
    public void secured_page_should_require_login() {
        // when the /secured route is opened
        getDriver().get(getRootURL() + "/secured");

        // then it redirects to the default login page
        waitUntil(ExpectedConditions.urlToBe(getRootURL() + "/login"));

        // when the user logs in
        findElement(By.id("username")).sendKeys("user");
        findElement(By.id("password")).sendKeys("user");
        findElement(By.tagName("button")).click();

        // then it redirects to /secured and there are no client errors
        waitUntil(ExpectedConditions.urlToBe(getRootURL() + "/secured"));
        Assert.assertNotNull(findElement(By.id("secured")));
        checkLogsForErrors();
    }

    @Test
    public void static_resources_accessible_without_login() throws Exception {
        verifyResourceAvailable("/images/image.png");
        verifyResourceAvailable("/icons/icon.png");
    }

    private void verifyResourceAvailable(String path) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(getRootURL() + path);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        try {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        } finally {
            response.close();
        }
    }
}
