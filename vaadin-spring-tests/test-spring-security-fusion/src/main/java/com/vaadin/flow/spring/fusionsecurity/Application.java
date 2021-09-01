package com.vaadin.flow.spring.fusionsecurity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;


@PWA(name = "Spring Security Helper Test Project", shortName = "SSH Test")
@SpringBootApplication
@Theme("spring-security-test-app")
@NpmPackage(value = "@adobe/lit-mobx",
            version = "1.0.1")
@NpmPackage(value = "mobx",
            version = "6.2.0")
public class Application implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
