package com.vaadin.flow.connect;

import com.vaadin.flow.server.connect.Endpoint;
import com.vaadin.flow.server.auth.AnonymousAllowed;

/**
 * Simple Vaadin Connect Service definition.
 */
@Endpoint
public class AppEndpoint {
    @AnonymousAllowed
    public String helloAnonymous() {
        return "Hello, stranger!";
    }
}
