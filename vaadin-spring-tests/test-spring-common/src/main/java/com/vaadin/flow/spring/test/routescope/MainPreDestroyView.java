package com.vaadin.flow.spring.test.routescope;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@PreserveOnRefresh
@Route(value = "preserve-pre-destroy")
public class MainPreDestroyView extends Div {

    @Autowired
    private DestroyBean bean;

    public MainPreDestroyView() {
        setId("main");
        RouterLink link = new RouterLink("navigate to not preserved view",
                PreDestroyView.class);
        add(link);
        link.setId("navigate-out");
    }
}