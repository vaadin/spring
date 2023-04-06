package com.vaadin.flow.connect;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.WildcardParameter;

@Route(value = "")
public class MainView extends Div implements HasUrlParameter<String> {

    public MainView() {
        add(new TestComponent());
    }

    @Override
    public void setParameter(BeforeEvent event,
            @WildcardParameter String parameter) {
        // no op. Implement HasUrlParameter to test deeper levels of url.
    }
}
