package com.vaadin.flow.spring.test.routescope;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.RouteScopeOwner;

@RouteScope
@RouteScopeOwner(CustomExceptionTarget.class)
@Component
public class CustomExceptionSubDiv extends Div {

    public CustomExceptionSubDiv() {
        setId("custom-exception-div");
        setText(UUID.randomUUID().toString());
    }
}
