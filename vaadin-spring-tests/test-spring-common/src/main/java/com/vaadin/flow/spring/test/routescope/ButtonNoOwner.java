package com.vaadin.flow.spring.test.routescope;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.spring.annotation.RouteScope;

@RouteScope
@Component
public class ButtonNoOwner extends NativeButton {

    public ButtonNoOwner() {
        setId("no-owner-button");
        setText(UUID.randomUUID().toString());
    }
}