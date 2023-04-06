package com.vaadin.flow.spring.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

import com.vaadin.flow.spring.SpringBootAutoConfiguration;

@Configuration
@AutoConfigureBefore(SpringBootAutoConfiguration.class)
@ConditionalOnClass(SpringBootAutoConfiguration.class)
public class ProfileEnableInitializer {

    @Autowired
    private ConfigurableEnvironment env;

    @Bean(name = "profileContextInit")
    public ServletContextInitializer contextInitializer() {
        env.setActiveProfiles("enabled");
        return context -> {
        };
    }

}
