package com.vaadin.flow.spring.test.routescope;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.spring.annotation.RouteScope;

@RouteScope
@Component
public class DivNoOwner extends Div {

    public DivNoOwner() {
        setId("no-owner-div");
        setText(UUID.randomUUID().toString());
    }
}
