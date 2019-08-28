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
