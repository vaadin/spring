package com.vaadin.flow.spring.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.vaadin.flow.server.VaadinService;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

/**
 * A version of {@link SavedRequestAwareAuthenticationSuccessHandler} that
 * writes a different return value for a Fusion TypeScript client.
 * <p>
 * This class acts as a {@link SavedRequestAwareAuthenticationSuccessHandler}
 * unless the request from the client contains a "source: typescript" header.
 * <p>
 * If the header is present, it sends a return value that is an "ok" instead of
 * a "redirect" response. This is so that the TypeScript caller is able to read
 * the returned values. Additionally it sends the saved URL separately so the
 * client can decide where to redirect if no URL was saved.
 */
public class VaadinSavedRequestAwareAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    /**
     * Redirect strategy used by
     * {@link VaadinSavedRequestAwareAuthenticationSuccessHandler}.
     */
    public static class TypeScriptClientRedirectStrategy extends DefaultRedirectStrategy {
        @Override
        public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url)
                throws IOException {
            if (!isTypescriptLogin(request)) {
                super.sendRedirect(request, response, url);
                return;
            }

            response.setHeader("Result", "success");
            HttpSession s = request.getSession(false);
            if (s != null) {
                String csrfToken = (String) s.getAttribute(VaadinService.getCsrfTokenAttributeName());
                if (csrfToken != null) {
                    response.setHeader("Vaadin-CSRF", csrfToken);
                }
            }
        }
    }

    /**
     * This needs to be stored only because the field in the super class is not
     * accessible.
     */
    private RequestCache requestCache = new HttpSessionRequestCache();

    /**
     * Creates a new instance.
     */
    public VaadinSavedRequestAwareAuthenticationSuccessHandler() {
        setRedirectStrategy(new TypeScriptClientRedirectStrategy());
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws ServletException, IOException {
        SavedRequest savedRequest = this.requestCache.getRequest(request, response);
        if (isTypescriptLogin(request)) {
            if (savedRequest != null) {
                response.setHeader("Saved-url", savedRequest.getRedirectUrl());
            }
            response.setHeader("Target-url", determineTargetUrl(request, response));
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }

    static boolean isTypescriptLogin(HttpServletRequest request) {
        return "typescript".equals(request.getHeader("source"));
    }

    @Override
    public void setRequestCache(RequestCache requestCache) {
        super.setRequestCache(requestCache);
        this.requestCache = requestCache;
    }
}
