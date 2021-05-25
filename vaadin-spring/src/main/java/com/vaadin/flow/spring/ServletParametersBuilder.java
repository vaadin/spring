/*
 * Copyright 2000-2017 Vaadin Ltd.
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

import java.util.HashMap;
import java.util.Map;

import com.vaadin.flow.server.InitParameters;

/**
 * This class is used to build the initial parameters map for the
 * VaadinServlet from a VaadinConfigurationProperties object, which is
 * automatically filled by Spring Boot from the application.properties file,
 * system or env properties.
 */
public class ServletParametersBuilder {

    /**
     * Creates a map with the initial parameters for the Vaadin servlet, from
     * a VaadinConfigurationProperties object
     *
     * @param configurationProperties the configuration object filled by spring boot
     * @return filled parameters map
     */
    public static Map<String, String> buildInitParametersFromConfig(
            VaadinConfigurationProperties configurationProperties) {
        Map<String, String> initParameters = new HashMap<>();

        if (configurationProperties.getProductionMode() != null)
            initParameters.put(InitParameters.SERVLET_PARAMETER_PRODUCTION_MODE,
                    "" + configurationProperties.getProductionMode());

        if (configurationProperties.getUseDeprecatedV14Bootstrapping() != null)
            initParameters.put(InitParameters.SERVLET_PARAMETER_USE_V14_BOOTSTRAP,
                    "" + configurationProperties.getUseDeprecatedV14Bootstrapping());

        if (configurationProperties.getRequestTiming() != null)
            initParameters.put(InitParameters.SERVLET_PARAMETER_REQUEST_TIMING,
                    "" + configurationProperties.getRequestTiming());

        if (configurationProperties.getDisableXsrfProtection() != null)
            initParameters.put(InitParameters.SERVLET_PARAMETER_DISABLE_XSRF_PROTECTION,
                    "" + configurationProperties.getDisableXsrfProtection());

        if (configurationProperties.getHeartbeatInterval() != null)
            initParameters.put(InitParameters.SERVLET_PARAMETER_HEARTBEAT_INTERVAL,
                    "" + configurationProperties.getHeartbeatInterval());

        if (configurationProperties.getCloseIdleSessions() != null)
            initParameters.put(InitParameters.SERVLET_PARAMETER_CLOSE_IDLE_SESSIONS,
                    "" + configurationProperties.getCloseIdleSessions());

        if (configurationProperties.getPushMode() != null)
            initParameters.put(InitParameters.SERVLET_PARAMETER_PUSH_MODE,
                    configurationProperties.getPushMode());

        if (configurationProperties.getPushURL() != null)
            initParameters.put(InitParameters.SERVLET_PARAMETER_PUSH_URL,
                    configurationProperties.getPushURL());

        if (configurationProperties.getSyncIdCheck() != null)
            initParameters.put(InitParameters.SERVLET_PARAMETER_SYNC_ID_CHECK,
                    "" + configurationProperties.getSyncIdCheck());

        if (configurationProperties.getSendUrlsAsParameters() != null)
            initParameters.put(InitParameters.SERVLET_PARAMETER_SEND_URLS_AS_PARAMETERS,
                    "" + configurationProperties.getSendUrlsAsParameters());

        if (configurationProperties.getPushLongPollingSuspendTimeout() != null)
            initParameters.put(InitParameters.SERVLET_PARAMETER_PUSH_SUSPEND_TIMEOUT_LONGPOLLING,
                    "" + configurationProperties.getPushLongPollingSuspendTimeout());

        if (configurationProperties.getMaxMessageSuspendTimeout() != null)
            initParameters.put(InitParameters.SERVLET_PARAMETER_MAX_MESSAGE_SUSPEND_TIMEOUT,
                    "" + configurationProperties.getMaxMessageSuspendTimeout());


        if (configurationProperties.getDevmodeOptimizeBundle() != null)
            initParameters.put(InitParameters.SERVLET_PARAMETER_DEVMODE_OPTIMIZE_BUNDLE,
                    "" + configurationProperties.getDevmodeOptimizeBundle());

        if (configurationProperties.getPnpmEnable() != null)
            initParameters.put(InitParameters.SERVLET_PARAMETER_ENABLE_PNPM,
                    "" + configurationProperties.getPnpmEnable());

        if (configurationProperties.getDevmodeLiveReloadEnabled() != null)
            initParameters.put(InitParameters.SERVLET_PARAMETER_DEVMODE_ENABLE_LIVE_RELOAD,
                    "" + configurationProperties.getDevmodeLiveReloadEnabled());

        return initParameters;
    }

}
