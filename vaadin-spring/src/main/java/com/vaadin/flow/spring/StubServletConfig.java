package com.vaadin.flow.spring;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.DeploymentConfigurationFactory;

/**
 * Default ServletConfig implementation.
 */
@SuppressWarnings("rawtypes")
public class StubServletConfig implements ServletConfig {

    private final ServletContext context;
    private final ServletRegistrationBean registration;
    private final ApplicationContext appContext;

    /**
     * Constructor.
     *
     * @param context
     *         the ServletContext
     * @param registration
     *         the ServletRegistration for this ServletConfig instance
     */
    private StubServletConfig(ServletContext context,
            ServletRegistrationBean registration,
            ApplicationContext appContext) {
        this.context = context;
        this.registration = registration;
        this.appContext = appContext;
    }

    @Override
    public String getServletName() {
        return registration.getServletName();
    }

    @Override
    public ServletContext getServletContext() {
        return context;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String getInitParameter(String name) {
        Environment env = appContext.getBean(Environment.class);
        String propertyValue = env.getProperty("vaadin." + name);
        if (propertyValue != null) {
            return propertyValue;
        }

        return ((Map<String, String>) registration.getInitParameters())
                .get(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Enumeration<String> getInitParameterNames() {
        Environment env = appContext.getBean(Environment.class);
        // Collect any vaadin.XZY properties from application.properties
        List<String> initParameters = SpringServlet.PROPERTY_NAMES.stream()
                .filter(name -> env.getProperty("vaadin." + name) != null)
                .collect(Collectors.toList());
        initParameters.addAll(registration.getInitParameters().keySet());
        return Collections.enumeration(initParameters);
    }

    /**
     * Creates a DeploymentConfiguration.
     *
     * @param context
     *         the ServletContext
     * @param registration
     *         the ServletRegistrationBean to get servlet parameters from
     * @param servletClass
     *         the class to look for properties defined with annotations
     * @return a DeploymentConfiguration instance
     */
    public static DeploymentConfiguration createDeploymentConfiguration(
            ServletContext context, ServletRegistrationBean registration,
            Class<?> servletClass, ApplicationContext appContext) {
        try {
            ServletConfig servletConfig = new StubServletConfig(context,
                    registration, appContext);
            return DeploymentConfigurationFactory
                    .createPropertyDeploymentConfiguration(servletClass,
                            servletConfig);
        } catch (ServletException e) {
            throw new IllegalStateException(String.format(
                    "Failed to get deployment configuration data for servlet with name '%s' and class '%s'",
                    registration.getServletName(), servletClass), e);
        }
    }
}