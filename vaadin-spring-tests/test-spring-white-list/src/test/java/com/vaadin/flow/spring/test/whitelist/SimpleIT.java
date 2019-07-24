/*
 * Copyright 2000-2019 Vaadin Ltd.
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

package com.vaadin.flow.spring.test.whitelist;

import com.vaadin.flow.testutil.ChromeBrowserTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class SimpleIT extends ChromeBrowserTest {
    @Before
    public void init() {
        open();
    }

    @Test
    public void simplePage_withWhiteList_works() {
        WebElement viewElement = findElement(By.tagName("simple-view"));
        WebElement button = findInShadowRoot(viewElement, By.id("button"))
                .get(0);
        button.click();

        WebElement log = findInShadowRoot(viewElement, By.id("log")).get(0);
        Assert.assertEquals(SimpleView.CLICKED_MESSAGE,
                log.getAttribute("value"));
        System.out.println(log.getAttribute("value"));
    }

    @Override
    protected String getTestPath() {
        return "/";
    }
}
