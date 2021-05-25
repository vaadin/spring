package com.vaadin.flow.spring.configuration;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.flow.server.InitParameters;
import com.vaadin.flow.spring.ServletParametersBuilder;
import com.vaadin.flow.spring.VaadinConfigurationProperties;

/*
These tests do not check if the configuration is
 */
public class ConfigurationTest {

    @Test
    public void hasConfiguration_productionMode_servletSet() {
        VaadinConfigurationProperties conf = new VaadinConfigurationProperties();
        conf.setProductionMode(true);
        Map<String, String> initParams = ServletParametersBuilder.buildInitParametersFromConfig(conf);
        Assert.assertEquals("true", initParams.get(InitParameters.SERVLET_PARAMETER_PRODUCTION_MODE));
    }

    @Test
    public void hasConfiguration_useDeprecatedV14Bootstrapping_servletSet() {
        VaadinConfigurationProperties conf = new VaadinConfigurationProperties();
        conf.setUseDeprecatedV14Bootstrapping(true);
        Map<String, String> initParams = ServletParametersBuilder.buildInitParametersFromConfig(conf);
        Assert.assertEquals("true", initParams.get(InitParameters.SERVLET_PARAMETER_USE_V14_BOOTSTRAP));
    }

    @Test
    public void hasConfiguration_requestTiming_servletSet() {
        VaadinConfigurationProperties conf = new VaadinConfigurationProperties();
        conf.setRequestTiming(true);
        Map<String, String> initParams = ServletParametersBuilder.buildInitParametersFromConfig(conf);
        Assert.assertEquals("true", initParams.get(InitParameters.SERVLET_PARAMETER_REQUEST_TIMING));
    }

    @Test
    public void hasConfiguration_disableXsrfProtection_servletSet() {
        VaadinConfigurationProperties conf = new VaadinConfigurationProperties();
        conf.setDisableXsrfProtection(true);
        Map<String, String> initParams = ServletParametersBuilder.buildInitParametersFromConfig(conf);
        Assert.assertEquals("true", initParams.get(InitParameters.SERVLET_PARAMETER_DISABLE_XSRF_PROTECTION));
    }

    @Test
    public void hasConfiguration_heartbeatInterval_servletSet() {
        VaadinConfigurationProperties conf = new VaadinConfigurationProperties();
        conf.setHeartbeatInterval(10);
        Map<String, String> initParams = ServletParametersBuilder.buildInitParametersFromConfig(conf);
        Assert.assertEquals("10", initParams.get(InitParameters.SERVLET_PARAMETER_HEARTBEAT_INTERVAL));
    }

    @Test
    public void hasConfiguration_closeIdleSessions_servletSet() {
        VaadinConfigurationProperties conf = new VaadinConfigurationProperties();
        conf.setCloseIdleSessions(true);
        Map<String, String> initParams = ServletParametersBuilder.buildInitParametersFromConfig(conf);
        Assert.assertEquals("true", initParams.get(InitParameters.SERVLET_PARAMETER_CLOSE_IDLE_SESSIONS));
    }

    @Test
    public void hasConfiguration_pushMode_servletSet() {
        VaadinConfigurationProperties conf = new VaadinConfigurationProperties();
        conf.setPushMode("normal");
        Map<String, String> initParams = ServletParametersBuilder.buildInitParametersFromConfig(conf);
        Assert.assertEquals("normal", initParams.get(InitParameters.SERVLET_PARAMETER_PUSH_MODE));
    }

    @Test
    public void hasConfiguration_pushURL_servletSet() {
        VaadinConfigurationProperties conf = new VaadinConfigurationProperties();
        conf.setPushURL("xfxfxf");
        Map<String, String> initParams = ServletParametersBuilder.buildInitParametersFromConfig(conf);
        Assert.assertEquals("xfxfxf", initParams.get(InitParameters.SERVLET_PARAMETER_PUSH_URL));
    }

    @Test
    public void hasConfiguration_syncIdCheck_servletSet() {
        VaadinConfigurationProperties conf = new VaadinConfigurationProperties();
        conf.setSyncIdCheck(true);
        Map<String, String> initParams = ServletParametersBuilder.buildInitParametersFromConfig(conf);
        Assert.assertEquals("true", initParams.get(InitParameters.SERVLET_PARAMETER_SYNC_ID_CHECK));
    }

    @Test
    public void hasConfiguration_sendUrlsAsParameters_servletSet() {
        VaadinConfigurationProperties conf = new VaadinConfigurationProperties();
        conf.setSendUrlsAsParameters(true);
        Map<String, String> initParams = ServletParametersBuilder.buildInitParametersFromConfig(conf);
        Assert.assertEquals("true", initParams.get(InitParameters.SERVLET_PARAMETER_SEND_URLS_AS_PARAMETERS));
    }

    @Test
    public void hasConfiguration_pushLongPollingSuspendTimeout_servletSet() {
        VaadinConfigurationProperties conf = new VaadinConfigurationProperties();
        conf.setPushLongPollingSuspendTimeout(123);
        Map<String, String> initParams = ServletParametersBuilder.buildInitParametersFromConfig(conf);
        Assert.assertEquals("123", initParams.get(InitParameters.SERVLET_PARAMETER_PUSH_SUSPEND_TIMEOUT_LONGPOLLING));
    }

    @Test
    public void hasConfiguration_maxMessageSuspendTimeout_servletSet() {
        VaadinConfigurationProperties conf = new VaadinConfigurationProperties();
        conf.setMaxMessageSuspendTimeout(512);
        Map<String, String> initParams = ServletParametersBuilder.buildInitParametersFromConfig(conf);
        Assert.assertEquals("512", initParams.get(InitParameters.SERVLET_PARAMETER_MAX_MESSAGE_SUSPEND_TIMEOUT));
    }

    @Test
    public void hasConfiguration_devmodeOptimizeBundle_servletSet() {
        VaadinConfigurationProperties conf = new VaadinConfigurationProperties();
        conf.setDevmodeOptimizeBundle(true);
        Map<String, String> initParams = ServletParametersBuilder.buildInitParametersFromConfig(conf);
        Assert.assertEquals("true", initParams.get(InitParameters.SERVLET_PARAMETER_DEVMODE_OPTIMIZE_BUNDLE));
    }

    @Test
    public void hasConfiguration_pnpmEnable_servletSet() {
        VaadinConfigurationProperties conf = new VaadinConfigurationProperties();
        conf.setPnpmEnable(true);
        Map<String, String> initParams = ServletParametersBuilder.buildInitParametersFromConfig(conf);
        Assert.assertEquals("true", initParams.get(InitParameters.SERVLET_PARAMETER_ENABLE_PNPM));
    }

    @Test
    public void hasConfiguration_devmodeLiveReloadEnabled_servletSet() {
        VaadinConfigurationProperties conf = new VaadinConfigurationProperties();
        conf.setDevmodeLiveReloadEnabled(true);
        Map<String, String> initParams = ServletParametersBuilder.buildInitParametersFromConfig(conf);
        Assert.assertEquals("true", initParams.get(InitParameters.SERVLET_PARAMETER_DEVMODE_ENABLE_LIVE_RELOAD));
    }


}
