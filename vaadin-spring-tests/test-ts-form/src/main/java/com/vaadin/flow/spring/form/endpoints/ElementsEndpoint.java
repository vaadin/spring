package com.vaadin.flow.spring.form.endpoints;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.flow.server.connect.Endpoint;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.form.data.Elements;
import com.vaadin.flow.spring.form.data.Elements.Options;

@Endpoint
@AnonymousAllowed
public class ElementsEndpoint {

    public Elements getElements() {
     return new Elements();
    }

    public List<String> getOptions() {
        return Stream.of(Options.values()).map(Enum::toString).collect(Collectors.toList());
    }

    public Elements saveElements(Elements item) {
        return item;
    }
}

