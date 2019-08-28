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
package com.vaadin.flow.spring.test;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.TestBenchElement;

public class DoubleNpmAnnotationIT extends AbstractSpringTest {

    @Test
    public void bothPaperWebComponentsAreLoaded() throws Exception {
        open();
        TestBenchElement paperCheckbox = $("paper-checkbox").first();
        TestBenchElement paperInput = $("paper-input").first();

        // check that elements are on the page
        Assert.assertNotNull(paperCheckbox);
        Assert.assertNotNull(paperInput);

        // verify that the paper components are upgraded
        Assert.assertNotNull(paperCheckbox.$("paper-input-container"));
        Assert.assertNotNull(paperInput.$("checkboxContainer"));
    }

    @Override
    protected String getTestPath() {
        return "/double-npm-annotation";
    }
}
