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


import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Configuration properties for Vaadin Spring Boot.
 *
 * @author Vaadin Ltd
 * @see <a href=
 * "http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html">http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html</a>
 */
@ConfigurationProperties(prefix = "vaadin")
public class VaadinConfigurationProperties {

    /**
     * Base URL mapping of the Vaadin servlet.
     */
    private String urlMapping = "/*";

    /**
     * Whether asynchronous operations are supported.
     */
    private boolean asyncSupported = true;

    /**
     * Whether pnpm support is enabled.
     **/
    private boolean pnpmEnabled = false;

    /**
     * List of blocked packages that shouldn't be scanned.
     */
    private List<String> blockedPackages = new ArrayList<>();

    /**
     * List of allowed packages that should be scanned.
     */
    private List<String> allowedPackages = new ArrayList<>();

    /**
     * Gets the url mapping for the Vaadin servlet.
     *
     * @return the url mapping
     */
    public String getUrlMapping() {
        return urlMapping;
    }

    /**
     * Sets {@code urlMapping} property value.
     *
     * @param urlMapping the {@code urlMapping} property value
     */
    public void setUrlMapping(String urlMapping) {
        this.urlMapping = urlMapping;
    }

    /**
     * Returns if asynchronous operations are supported.
     *
     * @return if async is supported
     */
    public boolean isAsyncSupported() {
        return asyncSupported;
    }

    /**
     * Sets {@code asyncSupported} property value.
     *
     * @param asyncSupported the {@code asyncSupported} property value
     */
    public void setAsyncSupported(boolean asyncSupported) {
        this.asyncSupported = asyncSupported;
    }

    /**
     * Returns if pnpm support is enabled.
     *
     * @return if pnpm is enabled
     */
    public boolean isPnpmEnabled() {
        return pnpmEnabled;
    }

    /**
     * Sets {@code pnpmEnabled} property value.
     *
     * @param pnpmEnabled the {@code pnpmEnabled} property value
     */
    public void setPnpmEnabled(boolean pnpmEnabled) {
        this.pnpmEnabled = pnpmEnabled;
    }

    /**
     * Get a list of packages that are blocked for class scanning.
     *
     * @return blocked packages
     * @deprecated use getBlockedPackages()
     */
    @Deprecated(forRemoval = true)
    public List<String> getBlacklistedPackages() {
        return Collections.unmodifiableList(blockedPackages);
    }

    /**
     * Get a list of packages that are blocked for class scanning.
     *
     * @return blocked packages
     */
    public List<String> getBlockedPackages() {
        return Collections.unmodifiableList(blockedPackages);
    }

    /**
     * Set a list of packages to ignore for class scanning.
     *
     * @param blockedPackages list of packages to ignore
     * @deprecated use setBlockedPackages()
     */
    @Deprecated(forRemoval = true)
    public void setBlacklistedPackages(List<String> blockedPackages) {
        this.blockedPackages = new ArrayList<>(blockedPackages);
    }

    /**
     * Set a list of packages to ignore for class scanning.
     *
     * @param blockedPackages list of packages to ignore
     */
    public void setBlockedPackages(List<String> blockedPackages) {
        this.blockedPackages = new ArrayList<>(blockedPackages);
    }

    /**
     * Get a list of packages that are allowed for class scanning.
     *
     * @return allowed packages
     * @deprecated use getAllowedPackages()
     */
    @Deprecated(forRemoval = true)
    public List<String> getWhitelistedPackages() {
        return Collections.unmodifiableList(allowedPackages);
    }

    /**
     * Get a list of packages that are allowed for class scanning.
     *
     * @return allowed packages
     */
    public List<String> getAllowedPackages() {
        return Collections.unmodifiableList(allowedPackages);
    }

    /**
     * Set list of packages to be scanned. If <code>allowedPackages</code>
     * is set then <code>blockedPackages</code> is ignored.
     *
     * @param allowedPackages list of packages to be scanned
     * @deprecated use setAllowedPackages()
     */
    @Deprecated(forRemoval = true)
    public void setWhitelistedPackages(List<String> allowedPackages) {
        this.allowedPackages = new ArrayList<>(allowedPackages);
    }

    /**
     * Set list of packages to be scanned. If <code>allowedPackages</code>
     * is set then <code>blockedPackages</code> is ignored.
     *
     * @param allowedPackages list of packages to be scanned
     */
    public void setAllowedPackages(List<String> allowedPackages) {
        this.allowedPackages = new ArrayList<>(allowedPackages);
    }
}
