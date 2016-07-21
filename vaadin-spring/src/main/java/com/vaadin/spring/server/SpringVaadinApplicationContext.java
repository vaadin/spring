package com.vaadin.spring.server;

import org.springframework.context.ApplicationContext;

/**
 * Holds current spring application context and prevents application to fail with
 * NotSerializableException during serialization of {@link com.vaadin.spring.server.SpringUIProvider}
 * and {@link com.vaadin.spring.navigator.SpringViewProvider}.
 *
 * @author Karsten Ludwig Hauser
 */
public class SpringVaadinApplicationContext {

    private static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        SpringVaadinApplicationContext.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

}
