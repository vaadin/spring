/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 * <p>
 * This program is available under Vaadin Commercial License and Service Terms.
 * <p>
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.spring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for Vaadin Spring Boot.
 *
 * @author Vaadin Ltd
 * @see <a href=
 *      "http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html">http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html</a>
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
     * Whether servlet is loaded on startup.
     */
    private boolean loadOnStartup = true;

    /**
     * Whether pnpm support is enabled
     **/
    private Pnpm pnpm = new Pnpm();

    /**
     * List of blocked packages that shouldn't be scanned.
     */
    private List<String> blockedPackages = new ArrayList<>();

    /**
     * List of allowed packages that should be scanned.
     */
    private List<String> allowedPackages = new ArrayList<>();

    /**
     * Whether a browser should be launched on startup when in development mode.
     */
    private boolean launchBrowser = false;

    public static class Pnpm {
        private boolean enable;

        /**
         * Returns if pnpm support is enabled.
         *
         * @return if pnpm is enabled
         */
        public boolean isEnable() {
            return enable;
        }

        /**
         * Enables/disabled pnp support.
         *
         * @param enable
         *            if {@code true} then pnpm support is enabled, otherwise
         *            it's disabled
         *
         */
        public void setEnable(boolean enable) {
            this.enable = enable;
        }

    }

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
     * @param urlMapping
     *            the {@code urlMapping} property value
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
     * @param asyncSupported
     *            the {@code asyncSupported} property value
     */
    public void setAsyncSupported(boolean asyncSupported) {
        this.asyncSupported = asyncSupported;
    }

    /**
     * Returns if servlet is loaded on startup.
     * <p>
     * If the servlet is not loaded on startup then the first request to the
     * server might be incorrectly handled by
     * {@link com.vaadin.flow.spring.security.VaadinWebSecurityConfigurerAdapter}
     * and access to a public view will be denied instead of allowed.
     *
     * @return if servlet is loaded on startup
     */
    public boolean isLoadOnStartup() {
        return loadOnStartup;
    }

    /**
     * Sets whether servlet is loaded on startup.
     * <p>
     * If the servlet is not loaded on startup then the first request to the
     * server might be incorrectly handled by
     * {@link com.vaadin.flow.spring.security.VaadinWebSecurityConfigurerAdapter}
     * and access to a public view will be denied instead of allowed.
     *
     * @param loadOnStartup
     *            {@code true} to load the servlet on startup, {@code false}
     *            otherwise
     */
    public void setLoadOnStartup(boolean loadOnStartup) {
        this.loadOnStartup = loadOnStartup;
    }

    /**
     * Returns if a browser should be launched on startup when in development
     * mode.
     * <p>
     *
     * @return if a browser should be launched on startup when in development
     *         mode
     */
    public boolean isLaunchBrowser() {
        return launchBrowser;
    }

    /**
     * Sets whether a browser should be launched on startup when in development
     * mode.
     *
     * @param launchBrowser
     *            {@code true} to launch a browser on startup when in
     *            development mode, {@code false} otherwise
     */
    public void setLaunchBrowser(boolean launchBrowser) {
        this.launchBrowser = launchBrowser;
    }

    /**
     * Returns if pnpm support is enabled.
     *
     * @return if pnpm is enabled
     */
    public boolean isPnpmEnabled() {
        return pnpm.isEnable();
    }

    /**
     * Enables/disabled pnpm support.
     *
     * @param enabled
     *            if {@code true} then pnpm support is enabled, otherwise it's
     *            disabled
     *
     */
    public void setPnpmEnabled(boolean enabled) {
        pnpm.setEnable(enabled);
    }

    /**
     * Get a list of packages that are blocked for class scanning.
     *
     * @return blocked packages
     * @deprecated use getBlockedPackages()
     */
    @Deprecated
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
    @Deprecated
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
    @Deprecated
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
    @Deprecated
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
