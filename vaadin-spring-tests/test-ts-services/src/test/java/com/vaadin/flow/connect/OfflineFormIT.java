/*
 * Copyright 2000-2017 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.flow.connect;

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

import com.vaadin.flow.testutil.ChromeDeviceTest;
import com.vaadin.testbench.TestBenchElement;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.mobile.NetworkConnection;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Class for testing issues in a spring-boot container.
 */
public class OfflineFormIT extends ChromeDeviceTest {

    private void openTestUrl(String url) {
        getDriver().get(getRootURL() + "/foo" + url);
    }

    @After
    public void tearDown(){
        // delete all the records
        openTestUrl("/list");
        TestBenchElement listView = $("person-list").first();
        listView.$(TestBenchElement.class).id("delete-all").click();
    }

    @Test
    public void should_saveFormOffline_and_submitTheOfflineForm_when_backOnline() throws IOException,
            InterruptedException {
        String firstName = savePersonOffline();

        // back online
        setConnectionType(NetworkConnection.ConnectionType.ALL);

        verifyPersonSavedInListView(firstName);
    }

    @Test
    public void should_saveFormOffline_and_submitTheOfflineForm_when_browserIsClosedAndReopenedInBetween() throws Exception {
        String firstName = savePersonOffline();

        closeCurrentWindowAndOpenANewOneOnline();

        verifyPersonSavedInListView(firstName);
    }

    private void closeCurrentWindowAndOpenANewOneOnline() throws IOException {
        String originalWindowHandle = getDriver().getWindowHandle();

        Collection<String> originalHandles = getDriver().getWindowHandles();

        ((JavascriptExecutor)getDriver()).executeScript("window.open();");

        Collection<String> newHandles = getDriver().getWindowHandles();

        newHandles.removeAll(originalHandles);

        String newHandle = newHandles.iterator().next();

        getDriver().switchTo().window(originalWindowHandle);

        // close the window
        getDriver().close();

        getDriver().switchTo().window(newHandle);

        setConnectionType(NetworkConnection.ConnectionType.ALL);
    }

    private void verifyPersonSavedInListView(String firstName) throws InterruptedException {
        // first navigate to the root view then to the list view
        // to give a bit time for the offline from to be submitted.
        openTestUrl("");
        openTestUrl("/list");

        TestBenchElement listView = $("person-list").first();
        verifyContent(listView, firstName);
    }

    private String savePersonOffline() throws IOException {
        openTestUrl("/form");
        TestBenchElement formView = $("person-form").first();
        waitForServiceWorkerReady();
        setConnectionType(NetworkConnection.ConnectionType.AIRPLANE_MODE);

        String firstName = UUID.randomUUID().toString();
        formView.$(TestBenchElement.class).id("first-name").sendKeys(firstName);
        formView.$(TestBenchElement.class).id("save").click();

        // check the alert text, which indicate online/offline submission
        waitUntil(ExpectedConditions.alertIsPresent());
        Assert.assertEquals("Person details stored offline", getDriver().switchTo().alert().getText());
        getDriver().switchTo().alert().accept();

        return firstName;
    }

    private void verifyContent(WebElement content, String expected) {
        waitUntil(
                ExpectedConditions.textToBePresentInElement(content, expected),
                25);
    }
}
