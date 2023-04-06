/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.spring;

import javax.servlet.MultipartConfigElement;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.ObjectProvider;
import java.util.HashMap;
import java.util.Map;

import com.vaadin.flow.spring.security.RequestUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * Spring boot auto-configuration class for Flow.
 *
 * @author Vaadin Ltd
 *
 */
@Configuration
@AutoConfigureBefore(WebMvcAutoConfiguration.class)
@ConditionalOnClass(ServletContextInitializer.class)
@EnableConfigurationProperties(VaadinConfigurationProperties.class)
@Import({ VaadinApplicationConfiguration.class,
        VaadinServletConfiguration.class })
public class SpringBootAutoConfiguration {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private VaadinConfigurationProperties configurationProperties;

    /**
     * Creates a {@link ServletContextInitializer} instance.
     *
     * @return a custom ServletContextInitializer instance
     */
    @Bean
    public ServletContextInitializer contextInitializer() {
        return new VaadinServletContextInitializer(context);
    }

    /**
     * Creates a {@link ServletRegistrationBean} instance with Spring aware
     * Vaadin servlet.
     *
     * @return a custom ServletRegistrationBean instance
     */
    @Bean
    public ServletRegistrationBean<SpringServlet> servletRegistrationBean(
            ObjectProvider<MultipartConfigElement> multipartConfig) {
        String mapping = configurationProperties.getUrlMapping();
        Map<String, String> initParameters = new HashMap<>();
        boolean rootMapping = RootMappedCondition.isRootMapping(mapping);
        if (rootMapping) {
            mapping = VaadinServletConfiguration.VAADIN_SERVLET_MAPPING;
        }
        ServletRegistrationBean<SpringServlet> registration = new ServletRegistrationBean<>(
                new SpringServlet(context, rootMapping), mapping);
        registration.setInitParameters(initParameters);
        registration
                .setAsyncSupported(configurationProperties.isAsyncSupported());
        registration.setName(
                ClassUtils.getShortNameAsProperty(SpringServlet.class));
        // Setup multi part form processing for non root servlet mapping to be
        // able to process Hilla login out of the box
        if (!rootMapping) {
            multipartConfig.ifAvailable(registration::setMultipartConfig);
        }
        registration.setLoadOnStartup(
                configurationProperties.isLoadOnStartup() ? 1 : -1);
        return registration;
    }

    /**
     * Deploys JSR-356 websocket endpoints when Atmosphere is available.
     *
     * @return the server endpoint exporter which does the actual work.
     */
    @Bean
    public ServerEndpointExporter websocketEndpointDeployer() {
        return new VaadinWebsocketEndpointExporter();
    }

}
