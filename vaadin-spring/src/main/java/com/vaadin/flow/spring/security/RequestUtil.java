package com.vaadin.flow.spring.security;

import javax.servlet.http.HttpServletRequest;

import com.vaadin.flow.server.HandlerHelper;
import com.vaadin.flow.spring.VaadinConfigurationProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Contains utility methods related to request handling.
 */
@Component
public class RequestUtil {

    @Autowired
    private VaadinConfigurationProperties configurationProperties;

    /**
     * Checks whether the request is an internal request.
     *
     * An internal request is one that is needed for all Vaadin applications to
     * function, e.g. UIDL or init requests.
     *
     * Note that bootstrap requests for any route or static resource requests are
     * not internal, neither are resource requests for the JS bundle.
     *
     * @param request the servlet request
     * @return {@code true} if the request is Vaadin internal, {@code false}
     *         otherwise
     */
    public boolean isFrameworkInternalRequest(HttpServletRequest request) {
        String vaadinMapping = configurationProperties.getUrlMapping();
        if (vaadinMapping.endsWith("/*")) {
            vaadinMapping = vaadinMapping.substring(0, vaadinMapping.length() - "/*".length());
        }
        return HandlerHelper.isFrameworkInternalRequest(vaadinMapping, request);
    }

}
