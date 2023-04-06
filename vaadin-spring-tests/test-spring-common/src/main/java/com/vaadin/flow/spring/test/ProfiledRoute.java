package com.vaadin.flow.spring.test;

import org.springframework.context.annotation.Profile;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;

@Route("profiled")
@Profile("enabled")
public class ProfiledRoute extends Div {

    public ProfiledRoute() {
        setId("profiled-enabled");
        setText("Profiled route is enabled");
    }

}
