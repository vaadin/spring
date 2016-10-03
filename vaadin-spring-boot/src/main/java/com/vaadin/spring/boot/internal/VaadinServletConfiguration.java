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
package com.vaadin.spring.boot.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.mvc.ServletForwardingController;

import com.vaadin.server.Constants;
import com.vaadin.server.VaadinServlet;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.server.SpringVaadinServlet;

/**
 * Spring configuration that sets up a
 * {@link com.vaadin.spring.server.SpringVaadinServlet}. If you want to
 * customize the servlet, extend it and make it available as a Spring bean.
 * <p>
 * By default, unless a custom mapping of the Vaadin servlet is performed using
 * the URL mapping configuration property
 * {@link VaadinServletConfigurationProperties#getUrlMapping()}, the Vaadin
 * servlet is mapped to a hidden path not to block requests destined to
 * {@link DispatcherServlet}. {@link ServletForwardingController} is then mapped
 * so that requests to all {@link SpringUI} paths are forwarded to the servlet
 * for the generation of a bootstrap page, which internally uses the Vaadin
 * servlet path for all other communication.
 * <p>
 * This approach currently relies on a hack that modifies request servlet path
 * and path info on the fly as those produced by
 * {@link ServletForwardingController} are not what {@link VaadinServlet}
 * expects. See {@link SpringVaadinServlet} for more information on this.
 * 
 * @author Petter Holmström (petter@vaadin.com)
 * @author Henri Sara (hesara@vaadin.com)
 */
@Configuration
@EnableConfigurationProperties(VaadinServletConfigurationProperties.class)
public class VaadinServletConfiguration implements InitializingBean {

    private static final String DEFAULT_SERVLET_URL_BASE = "/vaadinServlet";
    public static final String DEFAULT_SERVLET_URL_MAPPING = DEFAULT_SERVLET_URL_BASE
            + "/*";

    /**
     * Mapping for static resources that is used in case a non-default mapping
     * is used as the primary mapping.
     */
    public static final String STATIC_RESOURCES_URL_MAPPING = "/VAADIN/*";

    private static Logger logger = LoggerFactory
            .getLogger(VaadinServletConfiguration.class);

    @Autowired
    protected ApplicationContext applicationContext;
    @Autowired
    protected VaadinServletConfigurationProperties configurationProperties;

    // forward the root of all @SpringUIs to the Vaadin servlet
    @Bean
    public SimpleUrlHandlerMapping vaadinUiForwardingHandlerMapping() {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(Integer.MIN_VALUE + 1);

        Map<String, Object> urlMappings = new HashMap<String, Object>();

        if (isMappedToRoot()) {

            // map every @SpringUI both with and without trailing slash
            for (String path : getUIPaths()) {
                urlMappings.put("/" + path, vaadinUiForwardingController());
                if (path.length() > 0) {
                    urlMappings.put("/" + path + "/",
                            vaadinUiForwardingController());
                }
            }

            getLogger().info("Forwarding @SpringUI URLs from {}", urlMappings);

        }

        mapping.setUrlMap(urlMappings);

        return mapping;
    }

    protected List<String> getUIPaths() {
        List<String> uiMappings = new ArrayList<String>();
        logger.info("Checking the application context for Vaadin UI mappings");
        // more checks are performed by the UI provider
        final String[] uiBeanNames = applicationContext
                .getBeanNamesForAnnotation(SpringUI.class);
        for (String uiBeanName : uiBeanNames) {
            SpringUI annotation = applicationContext.findAnnotationOnBean(
                    uiBeanName, SpringUI.class);
            uiMappings.add(this.applicationContext.getEnvironment()
                    .resolvePlaceholders(annotation.path())
                    .replaceFirst("^/", ""));
        } 
        return uiMappings;
    }

    protected Logger getLogger() {
        return logger;
    }

    /**
     * Forwarding controller that sends requests for the root page of Vaadin
     * servlets to the Vaadin servlet.
     * 
     * @return forwarding controller
     */
    @Bean
    public Controller vaadinUiForwardingController() {
        VaadinServlet servlet = vaadinServlet();
        getLogger().info("Registering Vaadin servlet of type [{}]",
                servlet.getClass().getCanonicalName());
        ServletForwardingController controller = new ServletForwardingController();
        controller.setServletName(vaadinServletRegistration().getServletName());
        return controller;
    }

    /**
     * Returns true if the Vaadin servlet is mapped to the context root, false
     * otherwise.
     * 
     * @return true if the Vaadin servlet is mapped to the context root
     */
    protected boolean isMappedToRoot() {
        String prefix = configurationProperties.getUrlMapping();
        if (prefix == null) {
            return true;
        }
        // strip trailing slash or /* to be tolerant of different user input
        prefix = prefix.trim().replaceAll("(/\\**)?$", "");
        return "".equals(prefix);
    }

    protected String[] getUrlMappings() {
        // the Vaadin servlet is not at context root to allow DispatcherServlet
        // to work
        if (isMappedToRoot()) {
            return new String[] { DEFAULT_SERVLET_URL_MAPPING,
                    STATIC_RESOURCES_URL_MAPPING };
        } else {
            String mapping = configurationProperties.getUrlMapping();
            String baseMapping = mapping.trim().replaceAll("(/\\**)?$", "");
            return new String[] { baseMapping, baseMapping + "/*",
                    STATIC_RESOURCES_URL_MAPPING };
        }
    }

    @Bean
    protected ServletRegistrationBean vaadinServletRegistration() {
        return createServletRegistrationBean();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        getLogger().debug("{} initialized", getClass().getName());
    }

    @Bean
    @ConditionalOnMissingBean
    public VaadinServlet vaadinServlet() {
        return new SpringVaadinServlet();
    }

    protected ServletRegistrationBean createServletRegistrationBean() {
        getLogger().info("Registering Vaadin servlet");
        final String[] urlMappings = getUrlMappings();
        getLogger().info("Servlet will be mapped to URLs {}",
                (Object) urlMappings);
        final VaadinServlet servlet = vaadinServlet();

        // this is a hack to make is possible for Vaadin and Spring MVC
        // applications to co-exist in the same global "namespace"
        if (servlet instanceof SpringVaadinServlet && isMappedToRoot()) {
            SpringVaadinServlet vaadinServlet = (SpringVaadinServlet) servlet;
            vaadinServlet.setServiceUrlPath(DEFAULT_SERVLET_URL_BASE);
        }

        final ServletRegistrationBean registrationBean = new ServletRegistrationBean(
                servlet, urlMappings);
        addInitParameters(registrationBean);
        return registrationBean;
    }

    protected void addInitParameters(
            ServletRegistrationBean servletRegistrationBean) {
        getLogger().info("Setting servlet init parameters");

        addInitParameter(servletRegistrationBean,
                Constants.SERVLET_PARAMETER_PRODUCTION_MODE,
                String.valueOf(configurationProperties.isProductionMode()));
        addInitParameter(servletRegistrationBean,
                Constants.SERVLET_PARAMETER_RESOURCE_CACHE_TIME,
                String.valueOf(configurationProperties.getResourceCacheTime()));
        addInitParameter(servletRegistrationBean,
                Constants.SERVLET_PARAMETER_HEARTBEAT_INTERVAL,
                String.valueOf(configurationProperties.getHeartbeatInterval()));
        addInitParameter(servletRegistrationBean,
                Constants.SERVLET_PARAMETER_CLOSE_IDLE_SESSIONS,
                String.valueOf(configurationProperties.isCloseIdleSessions()));

        addInitParameter(servletRegistrationBean,
                Constants.PARAMETER_VAADIN_RESOURCES,
                configurationProperties.getResources());
    }

    private void addInitParameter(
            ServletRegistrationBean servletRegistrationBean, String paramName,
            String propertyValue) {
        if (propertyValue != null) {
            getLogger().info("Set servlet init parameter [{}] = [{}]",
                    paramName, propertyValue);
            servletRegistrationBean.addInitParameter(paramName, propertyValue);
        }
    }

}
