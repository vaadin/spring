package com.vaadin.flow.spring.test;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.vaadin.flow.spring.annotation.UIScope;

@Component
@UIScope
public class UIScopedBean {

    private final String uid = UUID.randomUUID().toString();

    public String getUid() {
        return uid;
    }
}
