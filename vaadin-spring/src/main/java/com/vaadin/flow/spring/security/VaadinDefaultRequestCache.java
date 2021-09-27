package com.vaadin.flow.spring.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

/**
 * A default request cache implementation which aims to ignore requests that are
 * not for routes.
 *
 * Using this class helps with redirecting the user to the correct route after
 * login instead of redirecting to some internal URL like a service worker or
 * some data the service worker has fetched.
 */
@Component
public class VaadinDefaultRequestCache implements RequestCache {

    @Autowired
    private RequestUtil requestUtil;

    private RequestCache delegateRequestCache = new HttpSessionRequestCache();

    @Override
    public void saveRequest(HttpServletRequest request,
            HttpServletResponse response) {
        if (requestUtil.isFrameworkInternalRequest(request)) {
            return;
        }
        if (requestUtil.isEndpointRequest(request)) {
            return;
        }
        if (isServiceWorkerInitiated(request)) {
            return;
        }

        LoggerFactory.getLogger(getClass())
                .debug("Saving request to " + request.getRequestURI());

        delegateRequestCache.saveRequest(request, response);
    }

    @Override
    public SavedRequest getRequest(HttpServletRequest request,
            HttpServletResponse response) {
        return delegateRequestCache.getRequest(request, response);
    }

    @Override
    public HttpServletRequest getMatchingRequest(HttpServletRequest request,
            HttpServletResponse response) {
        return delegateRequestCache.getMatchingRequest(request, response);
    }

    @Override
    public void removeRequest(HttpServletRequest request,
            HttpServletResponse response) {
        delegateRequestCache.removeRequest(request, response);
    }

    /**
     * Checks if the request is initiated by a service worker.
     *
     * NOTE This method can never be used for security purposes as the "Referer"
     * header is easy to fake.
     */
    private boolean isServiceWorkerInitiated(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        return referer != null && referer.endsWith("sw.js");
    }

    public void setDelegateRequestCache(RequestCache delegateRequestCache) {
        this.delegateRequestCache = delegateRequestCache;
    }
}