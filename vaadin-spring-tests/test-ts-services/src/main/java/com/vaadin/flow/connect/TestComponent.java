package com.vaadin.flow.connect;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.templatemodel.TemplateModel;

/**
 * A Test Web Component.
 */
@Tag("test-component")
@JsModule("./src/test-component.js")
public class TestComponent extends PolymerTemplate<TemplateModel> {

    @Id
    NativeButton button;
    @Id
    Div content;

    public TestComponent() {
        button.addClickListener(e -> {
            content.setText("Hello World");
        });
    }
}
