package com.vaadin.spring.demo;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.annotation.SpringView;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.UI;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by fireflyc@gmail.com on 15/3/6.
 */
@SpringUI
public class DemoUI extends UI {
    @Override
    protected void init(VaadinRequest request) {

    }

    @SpringView
    static public class DemoView extends CustomComponent implements View {
        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {

        }
    }

    @Configuration
    @ComponentScan("com.vaadin.spring.demo")
    static public class AutConfig {
    }
}
