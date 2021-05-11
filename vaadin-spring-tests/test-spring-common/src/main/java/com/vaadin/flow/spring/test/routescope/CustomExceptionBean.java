/*
 * Copyright 2000-2021 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.flow.spring.test.routescope;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.RouteScopeOwner;

@RouteScope
@RouteScopeOwner(CustomExceptionRoute.class)
@Component
public class CustomExceptionBean implements DisposableBean {

    private Registration listener;

    @PostConstruct
    private void postConstruct() {
        listener = UI.getCurrent().addAfterNavigationListener(event -> {
            Div div = new Div();
            div.setId("custom-exception-created");
            div.setText("custom exception bean is created");
            HasElement hasElement = event.getActiveChain().get(0);
            hasElement.getElement().appendChild(div.getElement());
            listener.remove();
        });
    }

    @Override
    public void destroy() throws Exception {
        listener = UI.getCurrent().addAfterNavigationListener(event -> {
            Div div = new Div();
            div.setId("custom-exception-destroyed");
            div.setText("custom exception bean is destroyed");
            HasElement hasElement = event.getActiveChain().get(0);
            hasElement.getElement().appendChild(div.getElement());
            listener.remove();
        });
    }

}
