package com.vaadin.flow.spring.test.routescope;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@Route("pre-destroy")
public class PreDestroyView extends Div {

    public PreDestroyView() {
        RouterLink link = new RouterLink("navigate to preserved view",
                MainPreDestroyView.class);
        add(link);
        link.setId("navigate-out");
    }

}
