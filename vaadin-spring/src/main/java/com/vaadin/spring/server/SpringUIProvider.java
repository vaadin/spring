/*
 * Copyright 2015 The original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vaadin.spring.server;

import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UICreateEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.TranslatedTitle;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.internal.UIID;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Vaadin {@link com.vaadin.server.UIProvider} that looks up UI classes from the
 * Spring application context. The UI classes must be annotated with
 * {@link com.vaadin.spring.annotation.SpringUI}.
 *
 * @author Petter Holmström (petter@vaadin.com)
 * @author Henri Sara (hesara@vaadin.com)
 */
public class SpringUIProvider extends UIProvider {

    private static final long serialVersionUID = 6954428459733726004L;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final WebApplicationContext webApplicationContext;
    private final Map<String, Class<? extends UI>> pathToUIMap = new ConcurrentHashMap<String, Class<? extends UI>>();
    private final Map<String, Class<? extends UI>> wildcardPathToUIMap = new ConcurrentHashMap<String, Class<? extends UI>>();

    public SpringUIProvider(WebApplicationContext webApplicationContext) {
        if (webApplicationContext == null) {
            throw new IllegalStateException(
                    "Spring WebApplicationContext not initialized for UI provider. Use e.g. ContextLoaderListener to initialize it.");
        }
        this.webApplicationContext = webApplicationContext;
        detectUIs();
        if (pathToUIMap.isEmpty()) {
            logger.warn("Found no Vaadin UIs in the application context");
        }
    }

    @SuppressWarnings("unchecked")
    protected void detectUIs() {
        logger.info("Checking the application context for Vaadin UIs");
        final String[] uiBeanNames = getWebApplicationContext()
                .getBeanNamesForAnnotation(SpringUI.class);
        for (String uiBeanName : uiBeanNames) {
            Class<?> beanType = getWebApplicationContext().getType(uiBeanName);
            if (UI.class.isAssignableFrom(beanType)) {
                logger.info("Found Vaadin UI [{}]", beanType.getCanonicalName());
                final String path;
                String tempPath = deriveMappingForUI(uiBeanName);
                if (tempPath.length() > 0 && !tempPath.startsWith("/")) {
                    path = "/".concat(tempPath);
                } else {
                    // remove terminal slash from mapping
                    path = tempPath.replaceAll("/$", "");
                }
                Class<? extends UI> existingBeanType = getUIByPath(path);
                if (existingBeanType != null) {
                    throw new IllegalStateException(String.format(
                            "[%s] is already mapped to the path [%s]",
                            existingBeanType.getCanonicalName(), path));
                }
                logger.debug("Mapping Vaadin UI [{}] to path [{}]",
                        beanType.getCanonicalName(), path);
                mapPathToUI(path, (Class<? extends UI>) beanType);
            }
        }
    }

    /**
     * Derive the name (path) for a UI based on its annotation parameters.
     *
     * If a path is given as a parameter for the annotation, it is used. An
     * empty string maps to the root context.
     *
     * @param uiBeanName
     *            name of the UI bean
     * @return path to map the UI to
     */
    protected String deriveMappingForUI(String uiBeanName) {
        SpringUI annotation = getWebApplicationContext().findAnnotationOnBean(
                uiBeanName, SpringUI.class);
        return annotation.path();
    }

    @Override
    public Class<? extends UI> getUIClass(
            UIClassSelectionEvent uiClassSelectionEvent) {
        final String path = extractUIPathFromRequest(uiClassSelectionEvent
                .getRequest());
        if (pathToUIMap.containsKey(path)) {
            return pathToUIMap.get(path);
        }

        for (Map.Entry<String, Class<? extends UI>> entry : wildcardPathToUIMap
                .entrySet()) {
            if (path.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }

        return null;
    }

    private String extractUIPathFromRequest(VaadinRequest request) {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.length() > 1) {
            String path = pathInfo;
            final int indexOfBang = path.indexOf('!');
            if (indexOfBang > -1) {
                path = path.substring(0, indexOfBang);
            }

            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
            return path;
        }
        return "";
    }

    protected WebApplicationContext getWebApplicationContext() {
        return webApplicationContext;
    }

    protected void mapPathToUI(String path, Class<? extends UI> uiClass) {
        if (path.endsWith("/*")) {
            wildcardPathToUIMap.put(path.substring(0, path.length() - 2),
                    uiClass);
        } else {
            pathToUIMap.put(path, uiClass);
        }
    }

    protected Class<? extends UI> getUIByPath(String path) {
        return pathToUIMap.get(path);
    }

    @Override
    public UI createInstance(UICreateEvent event) {
        final Class<UIID> key = UIID.class;
        final UIID identifier = new UIID(event);
        CurrentInstance.set(key, identifier);
        try {
            logger.debug(
                    "Creating a new UI bean of class [{}] with identifier [{}]",
                    event.getUIClass().getCanonicalName(), identifier);
            return webApplicationContext.getBean(event.getUIClass());
        } finally {
            CurrentInstance.set(key, null);
        }
    }

    @Override
    public String getPageTitle(UICreateEvent event) {
        final String pageTitle = super.getPageTitle(event);
        return pageTitle == null ? getPageTitleByMessageSource(event) : pageTitle;
    }

    private String getPageTitleByMessageSource(UICreateEvent event) {
        TranslatedTitle translatedTitleAnnotation = getAnnotationFor(event.getUIClass(), TranslatedTitle.class);
        if (translatedTitleAnnotation == null) {
            return null;
        } else {
            String messageCode = translatedTitleAnnotation.key();
            String defaultText = translatedTitleAnnotation.defaultValue() == null
                    ?  "?" + messageCode + "?" : translatedTitleAnnotation.defaultValue();
            return webApplicationContext.getMessage(messageCode, null, defaultText, event.getRequest().getLocale());
        }
    }

}
