package com.vaadin.flow.spring.fusionsecurityjwt;

import com.vaadin.flow.spring.fusionsecurity.SecurityConfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(excludeFilters = 
        {@ComponentScan.Filter(type=FilterType.ASSIGNABLE_TYPE, value = SecurityConfig.class)})
public class Application extends com.vaadin.flow.spring.fusionsecurity.Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
