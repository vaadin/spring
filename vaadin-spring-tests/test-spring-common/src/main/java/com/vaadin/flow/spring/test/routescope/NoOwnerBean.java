package com.vaadin.flow.spring.test.routescope;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.vaadin.flow.spring.annotation.RouteScope;

@RouteScope
@Component
public class NoOwnerBean {

    private final String value = UUID.randomUUID().toString();

    public String getValue() {
        return value;
    }
}
