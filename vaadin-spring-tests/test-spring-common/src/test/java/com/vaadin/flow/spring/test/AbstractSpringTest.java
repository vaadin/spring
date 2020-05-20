package com.vaadin.flow.spring.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.vaadin.flow.testutil.ChromeBrowserTest;
import org.junit.Before;
import org.junit.BeforeClass;

public abstract class AbstractSpringTest extends ChromeBrowserTest {

    @Before
    public void setup() throws Exception {
        super.setup();
        if (System.getProperty("bowerMode") != null) {
            System.setProperty("vaadin.compatibilityMode", "true");
        }
    }

    @Override
    protected String getTestURL(String... parameters) {
        return getTestURL(getRootURL(), getContextPath() + getTestPath(),
                parameters);
    }

    protected String getContextRootURL() {
        return getRootURL() + getContextPath();
    }

    protected String getContextPath() {
        Properties p = new Properties();
        try {
            InputStream res = getClass()
                    .getResourceAsStream("/application.properties");
            if (res != null) {
                p.load(res);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String contextPath = p.getProperty("server.servlet.contextPath");
        if (contextPath != null) {
            return contextPath;
        } else {
            return "";
        }
    }

}
