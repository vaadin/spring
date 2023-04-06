package com.vaadin.flow.spring.test.routescope;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@Route("another-no-owner")
public class AnotherBeanNopOwnerView extends Div {

    public AnotherBeanNopOwnerView(@Autowired DivNoOwner childDiv) {
        setId("another-no-owner");
        add(childDiv);

        RouterLink link = new RouterLink("no-owner-view",
                BeansWithNoOwnerView.class);
        link.getElement().getStyle().set("display", "block");
        link.setId("no-owner-view");
        add(link);
    }
}
