package com.vaadin.flow.spring.security;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.util.WebUtils;

class SerializedJwtSplitCookieRepository {
    public static final String JWT_HEADER_AND_PAYLOAD_COOKIE_NAME = "jwt.headerAndPayload";
    public static final String JWT_SIGNATURE_COOKIE_NAME = "jwt.signature";

    private long expiresIn = 1800L;

    SerializedJwtSplitCookieRepository() {
    }

    void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    String loadSerializedJwt(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        Cookie jwtHeaderAndPayload = WebUtils.getCookie(request,
                JWT_HEADER_AND_PAYLOAD_COOKIE_NAME);
        if (jwtHeaderAndPayload == null) {
            return null;
        }

        Cookie jwtSignature = WebUtils.getCookie(request,
                JWT_SIGNATURE_COOKIE_NAME);
        if (jwtSignature == null) {
            return null;
        }

        return jwtHeaderAndPayload.getValue() + "." + jwtSignature.getValue();
    }

    void saveSerializedJwt(String jwt, HttpServletRequest request,
            HttpServletResponse response) {
        if (jwt == null) {
            this.removeJwtSplitCookies(request, response);
        } else {
            this.setJwtSplitCookies(jwt, request, response);
        }
    }

    boolean containsSerializedJwt(HttpServletRequest request) {
        Cookie jwtHeaderAndPayload = WebUtils.getCookie(request,
                JWT_HEADER_AND_PAYLOAD_COOKIE_NAME);
        Cookie jwtSignature = WebUtils.getCookie(request,
                JWT_SIGNATURE_COOKIE_NAME);
        return (jwtHeaderAndPayload != null) && (jwtSignature != null);
    }

    private void setJwtSplitCookies(String jwt, HttpServletRequest request,
            HttpServletResponse response) {
        final String[] parts = jwt.split("\\.");
        final String jwtHeaderAndPayload = parts[0] + "." + parts[1];
        final String jwtSignature = parts[2];

        Cookie headerAndPayload = new Cookie(JWT_HEADER_AND_PAYLOAD_COOKIE_NAME,
                jwtHeaderAndPayload);
        headerAndPayload.setHttpOnly(false);
        headerAndPayload.setSecure(request.isSecure());
        headerAndPayload.setPath(request.getContextPath() + "/");
        headerAndPayload.setMaxAge((int) expiresIn - 1);
        response.addCookie(headerAndPayload);

        Cookie signature = new Cookie(JWT_SIGNATURE_COOKIE_NAME, jwtSignature);
        signature.setHttpOnly(true);
        signature.setSecure(request.isSecure());
        signature.setPath(request.getContextPath() + "/");
        signature.setMaxAge((int) expiresIn - 1);
        response.addCookie(signature);
    }

    private void removeJwtSplitCookies(HttpServletRequest request,
            HttpServletResponse response) {
        Cookie jwtHeaderAndPayloadRemove = new Cookie(
                JWT_HEADER_AND_PAYLOAD_COOKIE_NAME, null);
        jwtHeaderAndPayloadRemove.setPath(request.getContextPath() + "/");
        jwtHeaderAndPayloadRemove.setMaxAge(0);
        jwtHeaderAndPayloadRemove.setSecure(request.isSecure());
        jwtHeaderAndPayloadRemove.setHttpOnly(false);
        response.addCookie(jwtHeaderAndPayloadRemove);

        Cookie jwtSignatureRemove = new Cookie(JWT_SIGNATURE_COOKIE_NAME, null);
        jwtSignatureRemove.setPath(request.getContextPath() + "/");
        jwtSignatureRemove.setMaxAge(0);
        jwtSignatureRemove.setSecure(request.isSecure());
        jwtSignatureRemove.setHttpOnly(true);
        response.addCookie(jwtSignatureRemove);
    }
}
