package com.vaadin.flow.spring.test.routescope;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.RouteScopeOwner;

@RouteScope
@RouteScopeOwner(ButtonInLayout.class)
@Component
public class ButtonScopedBean implements DisposableBean {

    private Registration listener;

    @Override
    public void destroy() throws Exception {
        listener = UI.getCurrent().addAfterNavigationListener(event -> {
            Div div = new Div();
            div.setId("button-scoped-bean-destroy");
            div.setText("button scoped bean is destroyed");
            HasElement hasElement = event.getActiveChain().get(0);
            hasElement.getElement().appendChild(div.getElement());
            listener.remove();
        });
    }
}
