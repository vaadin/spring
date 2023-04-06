package com.vaadin.flow.spring.test;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;

@JsModule("./init-listener-component.js")
@Tag("init-listener-component")
public class ComponentAddedViaInitListenerView extends Component {

}
