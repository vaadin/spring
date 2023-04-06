package com.vaadin.flow.spring.test.routescope;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;

@PreserveOnRefresh
@Route("preserve-on-refresh")
public class PreserveOnRefreshRoute extends Div {

    @Autowired
    private ApplicationContext context;

    public PreserveOnRefreshRoute() {
        setId("preserve-on-refresh");
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        setText(context.getBean(PreserveOnRefreshBean.class).getValue());
    }
}
