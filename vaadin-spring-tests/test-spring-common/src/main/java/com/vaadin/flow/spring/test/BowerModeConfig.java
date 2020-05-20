package com.vaadin.flow.spring.test;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BowerModeConfig {

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            if (System.getProperty("bowerMode") != null) {
                System.setProperty("vaadin.compatibilityMode", "true");
            }
        };
    }
}
