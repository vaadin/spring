
package com.vaadin.flow.spring.test.whitelist;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.templatemodel.TemplateModel;

@Route("")
@Tag("simple-view")
@JsModule("./simple-view.js")
public class SimpleView extends PolymerTemplate<SimpleView.SimpleModel> {
    public interface SimpleModel extends TemplateModel {

    }

    public static final String CLICKED_MESSAGE = "Button clicked";

    @Id("button")
    Button button;

    @Id("log")
    TextField log;

    public SimpleView() {
        button.addClickListener(event -> log.setValue(CLICKED_MESSAGE));
    }

}
