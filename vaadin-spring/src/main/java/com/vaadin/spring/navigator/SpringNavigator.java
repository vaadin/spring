package com.vaadin.spring.navigator;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.NavigationStateManager;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.SingleComponentContainer;
import com.vaadin.ui.UI;

/**
 * A Navigator that automatically uses {@link SpringViewProvider} and allows
 * late initialization.
 *
 * @author Vaadin Ltd
 */
@UIScope
public class SpringNavigator extends Navigator {

    @Autowired
    private SpringViewProvider viewProvider;

    public void init(UI ui, ComponentContainer container) {
        init(ui, new ComponentContainerViewDisplay(container));
    }

    public void init(UI ui, SingleComponentContainer container) {
        init(ui, new SingleComponentContainerViewDisplay(container));
    }

    public void init(UI ui, ViewDisplay display) {
        init(ui, new UriFragmentManager(ui.getPage()), display);
    }

    @Override
    protected void init(UI ui, NavigationStateManager stateManager,
            ViewDisplay display) {
        super.init(ui, stateManager, display);
        addProvider(viewProvider);
    }

}