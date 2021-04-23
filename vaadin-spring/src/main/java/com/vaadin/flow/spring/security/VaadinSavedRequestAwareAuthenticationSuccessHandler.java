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
import org.springframework.security.web.csrf.CsrfToken;
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
     * If this header is present with a value of "typescript" in login requests,
     * this success handler is activated. Other requests are passed through to the
     * parent class.
     */
    private static final String SOURCE_HEADER = "source";

    /** This header contains 'ok' if login was successful. */
    private static final String RESULT_HEADER = "Result";

    /** This header contains the Vaadin CSRF token. */
    private static final String VAADIN_CSRF_HEADER = "Vaadin-CSRF";

    /**
     * This header contains the URL defined as the default URL to redirect to after
     * login.
     */
    private static final String DEFAULT_URL_HEADER = "Default-url";

    /**
     * This header contains the last URL saved by Spring Security. If the user
     * navigates to /private and is redirected to /login, this header will contain
     * "/private" after the login succeeds.
     */
    private static final String SAVED_URL_HEADER = "Saved-url";

    /**
     * This header contains the name of the request header Spring uses for its
     * CSRF token
     */
    private static final String SPRING_CSRF_HEADER = "Spring-CSRF-header";
    /**
     * This header contains the current Spring CSRF token
     */
    private static final String SPRING_CSRF_TOKEN = "Spring-CSRF-token";

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

            response.setHeader(RESULT_HEADER, "success");
            HttpSession session = request.getSession(false);
            if (session != null) {
                String csrfToken = (String) session.getAttribute(VaadinService.getCsrfTokenAttributeName());
                if (csrfToken != null) {
                    response.setHeader(VAADIN_CSRF_HEADER, csrfToken);
                }
            }
            Object springCsrfTokenObject = request.getAttribute("_csrf");
            if (springCsrfTokenObject != null
                    && springCsrfTokenObject instanceof CsrfToken) {
                CsrfToken springCsrfToken = (CsrfToken) springCsrfTokenObject;
                response.setHeader(SPRING_CSRF_HEADER,
                        springCsrfToken.getHeaderName());
                response.setHeader(SPRING_CSRF_TOKEN,
                        springCsrfToken.getToken());
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
                response.setHeader(SAVED_URL_HEADER, savedRequest.getRedirectUrl());
            }
            response.setHeader(DEFAULT_URL_HEADER, determineTargetUrl(request, response));
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }

    static boolean isTypescriptLogin(HttpServletRequest request) {
        return "typescript".equals(request.getHeader(SOURCE_HEADER));
    }

    @Override
    public void setRequestCache(RequestCache requestCache) {
        super.setRequestCache(requestCache);
        this.requestCache = requestCache;
    }
}
