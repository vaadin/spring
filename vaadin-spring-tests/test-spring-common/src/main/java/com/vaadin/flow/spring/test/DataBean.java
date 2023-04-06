package com.vaadin.flow.spring.test;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.vaadin.flow.spring.annotation.VaadinSessionScope;

/**
 * @author Vaadin Ltd
 *
 */
@Component
@VaadinSessionScope
public class DataBean {

    private final String uid = UUID.randomUUID().toString();

    public String getMessage() {
        return "foo" + uid;
    }
}
