/*
 * Copyright 2000-2017 Vaadin Ltd.
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
package com.vaadin.flow.spring;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.SessionDestroyListener;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WebBrowser;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.theme.AbstractTheme;

/**
 * Spring application context aware Vaadin servlet service implementation.
 *
 * @author Vaadin Ltd
 */
public class SpringVaadinServletService extends VaadinServletService {

    private transient final ApplicationContext context;

    private final Registration serviceDestroyRegistration;

    /**
     * Creates an instance connected to the given servlet and using the given
     * configuration with provided application {@code context}.
     *
     * @param servlet
     *         the servlet which receives requests
     * @param deploymentConfiguration
     *         the configuration to use
     * @param context
     *         the Spring application context
     */
    public SpringVaadinServletService(VaadinServlet servlet,
            DeploymentConfiguration deploymentConfiguration,
            ApplicationContext context) {
        super(servlet, deploymentConfiguration);
        this.context = context;
        SessionDestroyListener listener = event -> sessionDestroyed(
                event.getSession());
        Registration registration = addSessionDestroyListener(listener);
        serviceDestroyRegistration = addServiceDestroyListener(
                event -> serviceDestroyed(registration));
    }

    @Override
    protected Optional<Instantiator> loadInstantiators()
            throws ServiceException {
        Optional<Instantiator> spiInstantiator = super.loadInstantiators();
        List<Instantiator> springInstantiators = context
                .getBeansOfType(Instantiator.class).values().stream()
                .filter(instantiator -> instantiator.init(this))
                .collect(Collectors.toList());
        if (spiInstantiator.isPresent() && !springInstantiators.isEmpty()) {
            throw new ServiceException(
                    "Cannot init VaadinService because there are multiple eligible "
                            + "instantiator implementations: Java SPI registered instantiator "
                            + spiInstantiator.get()
                            + " and Spring instantiator beans: "
                            + springInstantiators);
        }
        if (!spiInstantiator.isPresent() && springInstantiators.isEmpty()) {
            Instantiator defaultInstantiator = new SpringInstantiator(this,
                    context);
            defaultInstantiator.init(this);
            return Optional.of(defaultInstantiator);
        }
        return spiInstantiator.isPresent() ? spiInstantiator
                : springInstantiators.stream().findFirst();
    }

    @Override
    protected VaadinSession createVaadinSession(VaadinRequest request) {
        return new SpringVaadinSession(this);
    }

    private void sessionDestroyed(VaadinSession session) {
        assert session instanceof SpringVaadinSession;
        ((SpringVaadinSession) session).fireSessionDestroy();
    }

    private void serviceDestroyed(Registration registration) {
        registration.remove();
        serviceDestroyRegistration.remove();
    }

    @Override
    public URL getStaticResource(String path) {
        URL resource = super.getStaticResource(path);
        if (resource == null) {
            resource = getResourceURL(path);
        }
        return resource;
    }

    @Override
    public URL getResource(String path, WebBrowser browser,
            AbstractTheme theme) {
        URL resource = super.getResource(path, browser, theme);
        if (resource == null) {
            resource = getResourceURL(getThemeResolvedPath(path, browser, theme));
        }
        return resource;
    }

    private URL getResourceURL(String path) {
        if (!isSpringBootConfigured()) {
            return null;
        }
        for (String prefix : context.getBean(
                org.springframework.boot.autoconfigure.web.ResourceProperties.class)
                .getStaticLocations()) {
            String location = (prefix + path).replaceAll("//", "/");
            Resource resource = context.getResource(location);
            if (resource != null) {
                try {
                    return resource.getURL();
                } catch (IOException e) {
                    // NO-OP file was not found.
                }
            }
        }
        return null;
    }

    private boolean isSpringBootConfigured() {
        String resourcePropertiesFQN = "org.springframework.boot.autoconfigure.web.ResourceProperties";
        if (isClassnameAvailable(resourcePropertiesFQN)) {
            String configurationPrefix = org.springframework.boot.autoconfigure.web.ResourceProperties.class
                    .getAnnotation(
                            org.springframework.boot.context.properties.ConfigurationProperties.class)
                    .prefix();
            return context.containsBean(
                    configurationPrefix + "-" + resourcePropertiesFQN);
        }
        return false;
    }

    private static boolean isClassnameAvailable(String clazzName) {
        try {
            Class.forName(clazzName, false,
                    SpringVaadinServletService.class.getClassLoader());
        } catch (LinkageError | ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    @Override
    public InputStream getResourceAsStream(String path, WebBrowser browser,
            AbstractTheme theme) {
        InputStream resourceAsStream = super
                .getResourceAsStream(path, browser, theme);
        if (resourceAsStream == null) {
            URL resourceURL = getResourceURL(
                    getThemeResolvedPath(path, browser, theme));
            if (resourceURL != null) {
                try {
                    resourceAsStream = resourceURL.openStream();
                } catch (IOException e) {
                    // NO-OP return null stream
                }
            }
        }
        return resourceAsStream;
    }


    private String getThemeResolvedPath(String url, WebBrowser browser,
            AbstractTheme theme) {
        String resourceUrl = resolveResource(url, browser);
        if (theme != null) {
            String themeUrl = theme.translateUrl(resourceUrl);
            if (!resourceUrl.equals(themeUrl)
                    && getResourceURL(themeUrl) != null) {
                return themeUrl;
            }
        }
        return resourceUrl;
    }
}
