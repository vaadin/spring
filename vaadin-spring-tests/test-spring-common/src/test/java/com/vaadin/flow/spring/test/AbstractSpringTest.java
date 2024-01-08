package com.vaadin.flow.spring.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;

import com.vaadin.flow.testutil.ChromeBrowserTest;
import org.junit.After;

public abstract class AbstractSpringTest extends ChromeBrowserTest {

    @Override
    protected String getTestURL(String... parameters) {
        return getTestURL(getRootURL(), getContextPath() + getTestPath(),
                parameters);
    }

    @After
    public void logConsoleErrors() {
        getLogEntries(Level.ALL)
                .forEach(le -> System.out.println("=================== " + le.getMessage()));
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
