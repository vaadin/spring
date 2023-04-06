package com.vaadin.flow.spring.test.routescope;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.RouteScopeOwner;

@RouteScope
@RouteScopeOwner(Layout.class)
@Component
public class MyService {

    private String value = UUID.randomUUID().toString();

    public String getValue() {
        return value;
    }
}
