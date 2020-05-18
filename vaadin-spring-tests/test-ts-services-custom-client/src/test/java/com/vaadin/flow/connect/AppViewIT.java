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

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.vaadin.flow.testutil.ChromeBrowserTest;
import com.vaadin.testbench.TestBenchElement;

/**
 * Class for testing issues in a spring-boot container.
 */
public class AppViewIT extends ChromeBrowserTest {

    private void openTestUrl(String url) {
        getDriver().get(getRootURL() + "/foo" + url);
    }

    private TestBenchElement mainView;
    private WebElement content;

    @Before
    public void setup() throws Exception {
        super.setup();
        openTestUrl("/");
        mainView = $("main-view").waitForFirst();
        content = mainView.$(TestBenchElement.class).id("content");
    }

    @Test
    public void should_requestAnonymously_connect_service() throws Exception {
        TestBenchElement button = mainView.$(TestBenchElement.class).id("helloAnonymous");
        button.click();

        // Wait for the server connect response
        verifyContent("Hello, stranger!");
    }

    private void verifyContent(String expected) {
        waitUntil(
                ExpectedConditions.textToBePresentInElement(content, expected),
                25);
    }
}
