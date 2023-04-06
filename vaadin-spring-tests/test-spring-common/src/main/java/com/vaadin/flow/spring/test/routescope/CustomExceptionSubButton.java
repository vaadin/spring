package com.vaadin.flow.spring.test.routescope;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.RouteScopeOwner;

@RouteScope
@RouteScopeOwner(CustomExceptionTarget.class)
@Component
public class CustomExceptionSubButton extends NativeButton {

    public CustomExceptionSubButton() {
        setId("custom-exception-button");
        setText(UUID.randomUUID().toString());
    }
}
