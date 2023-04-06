package com.vaadin.flow.spring.test;

import org.springframework.stereotype.Component;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;

@Route("foo")
@Component
@UIScope
public class FooNavigationTarget extends Div {

}
