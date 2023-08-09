package com.vaadin.flow.spring.test.routescope;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.RouteScopeOwner;

@Route("throw-exception")
public class CustomExceptionRoute extends Div {

    public CustomExceptionRoute(
            @Autowired @RouteScopeOwner(CustomExceptionRoute.class) CustomExceptionBean bean) {
        throw new CustomException();
    }

}