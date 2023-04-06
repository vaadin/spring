package com.vaadin.flow.spring.test;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;

public class NPEHandler extends Div
        implements HasErrorParameter<NullPointerException> {

    @Override
    public int setErrorParameter(BeforeEnterEvent event,
            ErrorParameter<NullPointerException> parameter) {
        getElement().setText("NPE is thrown");
        setId("npe-handle");
        LoggerFactory.getLogger(NPEHandler.class).error("NPE is thrown",
                parameter.getCaughtException());
        return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    }

}
