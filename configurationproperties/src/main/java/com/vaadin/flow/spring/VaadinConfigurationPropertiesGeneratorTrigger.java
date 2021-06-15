/*
 * Copyright 2000-2018 Vaadin Ltd.
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

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration properties generation trigger for Vaadin Spring Boot.
 *
 * Just add the fields and comments to generate the properties inside the
 * VaadinConfigurationProperties class. Please note that all the properties
 * stated in the com.vaadin.flow.server.InitParameters class will also be
 * included in the VaadinConfigurationProperties class.
 */
@SpringConfigurationPropertiesGenerator
public class VaadinConfigurationPropertiesGeneratorTrigger {

    /**
     * Base URL mapping of the Vaadin servlet.
     */
    private String urlMapping = "/*";

    /**
     * Whether asynchronous operations are supported.
     */
    private boolean asyncSupported = true;

    /**
     * Whether servlet is loaded on startup.
     */
    private boolean loadOnStartup = true;

    /**
     * Custom package blacklist that should be skipped in scanning.
     */
    private List<String> blacklistedPackages = new ArrayList<>();

    /**
     * Custom package whitelist that should be scanned.
     */
    private List<String> whitelistedPackages = new ArrayList<>();

}
