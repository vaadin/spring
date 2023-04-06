package com.vaadin.flow.spring.test;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * Test configuration which holds the package: all UI classes in the dependent
 * module are inside this package.
 *
 * @author Vaadin Ltd
 *
 */
@EnableAuthorizationServer
@Configuration
@EnableWebSecurity
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = ProfileEnableInitializer.class))
public class TestConfiguration extends WebMvcConfigurationSupport
        implements AuthorizationServerConfigurer {

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security)
            throws Exception {
        // doesn't need any impl
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients)
            throws Exception {
        clients.inMemory().build();
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints)
            throws Exception {
        // doesn't need any impl
    }

}
