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
     * Whether pnpm support is enabled
     **/
    private Pnpm pnpm = new Pnpm();

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
     * This is by default set to true, which means that if you are using some
     * live reload tool on the server side the browser is refreshed
     * automatically once code is reloaded on the server side.
     */
    private Boolean devmodeLiveReloadEnabled;




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
     * Returns the production mode
     *
     * @return the production mode
     */
    public Boolean getProductionMode() {
        return productionMode;
    }

    /**
     * Sets the production mode
     *
     * @param productionMode value for the production mode
     */
    public void setProductionMode(Boolean productionMode) {
        this.productionMode = productionMode;
    }

    /**
     * Returns the useDeprecatedV14Bootstrapping parameter
     *
     * @return the useDeprecatedV14Bootstrapping parameter
     */
    public Boolean getUseDeprecatedV14Bootstrapping() {
        return useDeprecatedV14Bootstrapping;
    }

    /**
     * Sets the useDeprecatedV14Bootstrapping parameter
     *
     * @param useDeprecatedV14Bootstrapping the useDeprecatedV14Bootstrapping parameter
     */
    public void setUseDeprecatedV14Bootstrapping(Boolean useDeprecatedV14Bootstrapping) {
        this.useDeprecatedV14Bootstrapping = useDeprecatedV14Bootstrapping;
    }

    /**
     * Returns the request timing parameter
     *
     * @return the request timing parameter
     */
    public Boolean getRequestTiming() {
        return requestTiming;
    }

    /**
     * Sets the request timing parameter
     *
     * @param requestTiming the request timing parameter
     */
    public void setRequestTiming(Boolean requestTiming) {
        this.requestTiming = requestTiming;
    }

    /**
     * Gets the disable xsrf protection parameter
     * @return the disable xsrf protection parameter
     */
    public Boolean getDisableXsrfProtection() {
        return disableXsrfProtection;
    }

    /**
     * Sets the disable xsrf protection parameter
     * @param disableXsrfProtection the disable xsrf protection parameter
     */
    public void setDisableXsrfProtection(Boolean disableXsrfProtection) {
        this.disableXsrfProtection = disableXsrfProtection;
    }

    /**
     * Gets the heart beat interval parameter
     *
     * @return the heart beat interval parameter
     */
    public Integer getHeartbeatInterval() {
        return heartbeatInterval;
    }

    /**
     * Sets the heart beat interval parameter
     *
     * @param heartbeatInterval the heart beat interval parameter
     */
    public void setHeartbeatInterval(Integer heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }

    /**
     * Gets the close idle sessions parameter
     *
     * @return the close idle sessions parameter
     */
    public Boolean getCloseIdleSessions() {
        return closeIdleSessions;
    }

    /**
     * Sets the close idle sessions parameter
     *
     * @param closeIdleSessions the close idle sessions parameter
     */
    public void setCloseIdleSessions(Boolean closeIdleSessions) {
        this.closeIdleSessions = closeIdleSessions;
    }

    /**
     * Gets the push mode parameter
     *
     * @return the push mode parameter
     */
    public String getPushMode() {
        return pushMode;
    }

    /**
     * Sets the push mode parameter
     *
     * @param pushMode the push mode parameter
     */
    public void setPushMode(String pushMode) {
        this.pushMode = pushMode;
    }

    /**
     * Gets the push url parameter
     *
     * @return the push url parameter
     */
    public String getPushURL() {
        return pushURL;
    }

    /**
     * Sets the push url parameter
     *
     * @param pushURL the push url parameter
     */
    public void setPushURL(String pushURL) {
        this.pushURL = pushURL;
    }

    /**
     * Gets the sync id check parameter
     *
     * @return the sync id check parameter
     */
    public Boolean getSyncIdCheck() {
        return syncIdCheck;
    }

    /**
     * Sets the sync id check parameter
     * @param syncIdCheck the sync id check parameter
     */
    public void setSyncIdCheck(Boolean syncIdCheck) {
        this.syncIdCheck = syncIdCheck;
    }

    /**
     * Gets the send url as parameters parameter
     *
     * @return the send url as parameters parameter
     */
    public Boolean getSendUrlsAsParameters() {
        return sendUrlsAsParameters;
    }

    /**
     * Sets the send url as parameters parameter
     *
     * @param sendUrlsAsParameters the send url as parameters parameter
     */
    public void setSendUrlsAsParameters(Boolean sendUrlsAsParameters) {
        this.sendUrlsAsParameters = sendUrlsAsParameters;
    }

    /**
     * Gets the push long polling suspend timeout parameter
     *
     * @return the push long polling suspend timeout parameter
     */
    public Integer getPushLongPollingSuspendTimeout() {
        return pushLongPollingSuspendTimeout;
    }

    /**
     * Sets the push long polling suspend timeout parameter
     *
     * @param pushLongPollingSuspendTimeout the push long polling suspend
     *                                      timeout parameter
     */
    public void setPushLongPollingSuspendTimeout(Integer pushLongPollingSuspendTimeout) {
        this.pushLongPollingSuspendTimeout = pushLongPollingSuspendTimeout;
    }

    /**
     * Gets the max message suspend timeout parameter
     *
     * @return the max message suspend timeout parameter
     */
    public Integer getMaxMessageSuspendTimeout() {
        return maxMessageSuspendTimeout;
    }

    /**
     * Sets the max message suspend timeout parameter
     *
     * @param maxMessageSuspendTimeout the max message suspend timeout parameter
     */
    public void setMaxMessageSuspendTimeout(Integer maxMessageSuspendTimeout) {
        this.maxMessageSuspendTimeout = maxMessageSuspendTimeout;
    }

    /**
     * Gets the dev mode optimize bundle parameter
     *
     * @return the dev mode optimize bundle parameter
     */
    public Boolean getDevmodeOptimizeBundle() {
        return devmodeOptimizeBundle;
    }

    /**
     * Sets the dev mode optimize bundle parameter
     *
     * @param devmodeOptimizeBundle the dev mode optimize bundle parameter
     */
    public void setDevmodeOptimizeBundle(Boolean devmodeOptimizeBundle) {
        this.devmodeOptimizeBundle = devmodeOptimizeBundle;
    }

    /**
     * Gets the dev mode live reload enabled parameter
     *
     * @return the dev mode live reload enabled parameter
     */
    public Boolean getDevmodeLiveReloadEnabled() {
        return devmodeLiveReloadEnabled;
    }

    /**
     * Sets the dev mode live reload enabled parameter
     *
     * @param devmodeLiveReloadEnabled the dev mode live reload enabled parameter
     */
    public void setDevmodeLiveReloadEnabled(Boolean devmodeLiveReloadEnabled) {
        this.devmodeLiveReloadEnabled = devmodeLiveReloadEnabled;
    }

}
