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
     *
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


    /**
     * Turns application to work in production mode. Production mode disables
     * most of the logged information that appears on the console because
     * logging and other debug features can have a significant performance
     * impact. Development mode JavaScript functions are not exported, push is
     * given as a minified JavaScript file instead of full size and static
     * resources are cached. See Deploying to Production for more information.
     */
    private Boolean productionMode;

    /**
     * This flag can be used to enable the server-side bootstrapping mode which
     * was used in Vaadin 14 and earlier version.
     */
    private Boolean useDeprecatedV14Bootstrapping;

    /**
     * If true, in each response the server includes some basic timing
     * information. This can be used for performance testing.
     */
    private Boolean requestTiming;

    /**
     * Cross-site request forgery protection. This protection is enabled by
     * default, but it might need to be disabled to allow a certain type of
     * testing. For these cases, the check can be disabled by setting the init
     * parameter.
     */
    private Boolean disableXsrfProtection;

    /**
     * Affects Flow applications only. UIs that are open on the client side
     * send a regular heartbeat to the server to indicate they are still alive,
     * even though there is no ongoing user interaction. When the server does
     * not receive a valid heartbeat for a given UI, it will eventually remove
     * that UI from the session.
     */
    private Integer heartbeatInterval;

    /**
     * When set to true (the default is false), the session is be closed if no UI is active. Heartbeat requests are like any other request from the servlet container’s viewpoint. This means that as long as there is an open UI, the session never expires even though there is no user interaction. You can control this behavior by setting an init parameter named closeIdleSessions to true.
     */
    private Boolean closeIdleSessions;

    /**
     * Affects Flow applications only. The permitted values are "disabled" or
     * "manual". See Server Push for more information.
     */
    private String pushMode;

    /**
     * Affects Flow applications only. The URL to use for push requests. Some
     * servers require a predefined URL to push. See Server Push for more
     * information.
     */
    private String pushURL;

    /**
     * Returns whether sync id checking is enabled. The sync id is used to
     * gracefully handle situations when the client sends a message to a
     * connector that has recently been removed on the server. Default is true.
     */
    private Boolean syncIdCheck;

    /**
     * Returns whether the sending of URL’s as GET and POST parameters in
     * requests with content-type application/x-www-form-urlencoded is enabled
     * or not.
     */
    private Boolean sendUrlsAsParameters;

    /**
     * Affects Flow applications only. When using the long polling transport
     * strategy, it specifies how long it accepts responses after each network
     * request. Number of milliseconds.
     */
    private Integer pushLongPollingSuspendTimeout;

    /**
     * In certain cases, such as when the server sends adjacent XmlHttpRequest
     * responses and push messages over a low bandwidth connection, messages
     * may be received out of order by the client. This property specifies the
     * maximum time (in milliseconds) that the client will then wait for the
     * predecessors of a received out-order message, before considering them
     * missing and requesting a full resynchronization of the application state
     * from the server. The default value is 5000 ms. You may increase this if
     * your application exhibits an undue amount of resynchronization requests
     * (as these degrade the UX due to flickering and loss of client-side-only
     * state such as scroll position).
     */
    private Integer maxMessageSuspendTimeout;

    /**
     * By default, in development mode all frontend resources found on the class
     * path are included in the generated webpack bundle. When set to true,
     * creates an optimized bundle by including only frontend resources that are
     * used from the application entry points (this is the default in production
     * mode). Uses bytecode scanning which increases application startup time.
     */
    private Boolean devmodeOptimizeBundle;

    /**
     * This flag can be used to enable pnpm instead of npm for resolving and
     * downloading frontend dependencies. By default it is false and npm is
     * used, but setting it to true enables pnpm. See how to switch between npm
     * and pnpm.
     */
    private Boolean pnpmEnable;

    /**
     * This is by default set to true, which means that if you are using some
     * live reload tool on the server side the browser is refreshed
     * automatically once code is reloaded on the server side.
     */
    private Boolean devmodeLiveReloadEnabled;

    /**
     * I18N provider property. To use localization and translation strings the
     * application only needs to implement I18NProvider and define the fully
     * qualified class name in the property i18n.provider. Please consult
     * Localization documentation.
     */
    private String i18nProvider;

    /**
     * Configuration name for the parameter that determines if Vaadin should
     * automatically register servlets needed for the application to work.
     */
    private Boolean disableAutomaticServletRegistration;




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
     * Get a list of packages that are blacklisted for class scanning.
     *
     * @return package blacklist
     */
    public List<String> getBlacklistedPackages() {
        return Collections.unmodifiableList(blacklistedPackages);
    }

    /**
     * Set list of packages to ignore for class scanning.
     *
     * @param blacklistedPackages
     *            list of packages to ignore
     */
    public void setBlacklistedPackages(List<String> blacklistedPackages) {
        this.blacklistedPackages = new ArrayList<>(blacklistedPackages);
    }

    /**
     * Get a list of packages that are white-listed for class scanning.
     *
     * @return package white-list
     */
    public List<String> getWhitelistedPackages() {
        return Collections.unmodifiableList(whitelistedPackages);
    }

    /**
     * Set list of packages to be scanned. If <code>whitelistedPackages</code>
     * is set then <code>blacklistedPackages</code> is ignored.
     *
     * @param whitelistedPackages
     *            list of packages to be scanned
     */
    public void setWhitelistedPackages(List<String> whitelistedPackages) {
        this.whitelistedPackages = new ArrayList<>(whitelistedPackages);
    }

    public Boolean getProductionMode() {
        return productionMode;
    }

    public void setProductionMode(Boolean productionMode) {
        this.productionMode = productionMode;
    }

    public Boolean getUseDeprecatedV14Bootstrapping() {
        return useDeprecatedV14Bootstrapping;
    }

    public void setUseDeprecatedV14Bootstrapping(Boolean useDeprecatedV14Bootstrapping) {
        this.useDeprecatedV14Bootstrapping = useDeprecatedV14Bootstrapping;
    }

    public Boolean getRequestTiming() {
        return requestTiming;
    }

    public void setRequestTiming(Boolean requestTiming) {
        this.requestTiming = requestTiming;
    }

    public Boolean getDisableXsrfProtection() {
        return disableXsrfProtection;
    }

    public void setDisableXsrfProtection(Boolean disableXsrfProtection) {
        this.disableXsrfProtection = disableXsrfProtection;
    }

    public Integer getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public void setHeartbeatInterval(Integer heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }

    public Boolean getCloseIdleSessions() {
        return closeIdleSessions;
    }

    public void setCloseIdleSessions(Boolean closeIdleSessions) {
        this.closeIdleSessions = closeIdleSessions;
    }

    public String getPushMode() {
        return pushMode;
    }

    public void setPushMode(String pushMode) {
        this.pushMode = pushMode;
    }

    public String getPushURL() {
        return pushURL;
    }

    public void setPushURL(String pushURL) {
        this.pushURL = pushURL;
    }

    public Boolean getSyncIdCheck() {
        return syncIdCheck;
    }

    public void setSyncIdCheck(Boolean syncIdCheck) {
        this.syncIdCheck = syncIdCheck;
    }

    public Boolean getSendUrlsAsParameters() {
        return sendUrlsAsParameters;
    }

    public void setSendUrlsAsParameters(Boolean sendUrlsAsParameters) {
        this.sendUrlsAsParameters = sendUrlsAsParameters;
    }

    public Integer getPushLongPollingSuspendTimeout() {
        return pushLongPollingSuspendTimeout;
    }

    public void setPushLongPollingSuspendTimeout(Integer pushLongPollingSuspendTimeout) {
        this.pushLongPollingSuspendTimeout = pushLongPollingSuspendTimeout;
    }

    public Integer getMaxMessageSuspendTimeout() {
        return maxMessageSuspendTimeout;
    }

    public void setMaxMessageSuspendTimeout(Integer maxMessageSuspendTimeout) {
        this.maxMessageSuspendTimeout = maxMessageSuspendTimeout;
    }

    public Boolean getDevmodeOptimizeBundle() {
        return devmodeOptimizeBundle;
    }

    public void setDevmodeOptimizeBundle(Boolean devmodeOptimizeBundle) {
        this.devmodeOptimizeBundle = devmodeOptimizeBundle;
    }

    public Boolean getPnpmEnable() {
        return pnpmEnable;
    }

    public void setPnpmEnable(Boolean pnpmEnable) {
        this.pnpmEnable = pnpmEnable;
    }

    public Boolean getDevmodeLiveReloadEnabled() {
        return devmodeLiveReloadEnabled;
    }

    public void setDevmodeLiveReloadEnabled(Boolean devmodeLiveReloadEnabled) {
        this.devmodeLiveReloadEnabled = devmodeLiveReloadEnabled;
    }

    public String getI18nProvider() {
        return i18nProvider;
    }

    public void setI18nProvider(String i18nProvider) {
        this.i18nProvider = i18nProvider;
    }

    public Boolean getDisableAutomaticServletRegistration() {
        return disableAutomaticServletRegistration;
    }

    public void setDisableAutomaticServletRegistration(Boolean disableAutomaticServletRegistration) {
        this.disableAutomaticServletRegistration = disableAutomaticServletRegistration;
    }
}
