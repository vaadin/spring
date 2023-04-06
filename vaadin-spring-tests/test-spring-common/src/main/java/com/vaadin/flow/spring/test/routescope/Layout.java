package com.vaadin.flow.spring.test.routescope;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;

public class Layout extends Div implements RouterLayout {

    public Layout() {
        add(createRouterLink("div", DivInLayout.class, "div-link"));
        add(createRouterLink("button", ButtonInLayout.class, "button-link"));
        add(createRouterLink("invalid", InvalidRouteScopeUsage.class,
                "invalid-route-link"));
    }

    private RouterLink createRouterLink(String text,
            Class<? extends Component> clazz, String id) {
        RouterLink link = new RouterLink(text, clazz);
        link.getStyle().set("display", "block");
        link.setId(id);
        return link;
    }
}
