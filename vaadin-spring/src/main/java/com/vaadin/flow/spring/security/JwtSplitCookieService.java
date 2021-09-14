package com.vaadin.flow.spring.security;

import javax.crypto.SecretKey;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.factories.DefaultJWSSignerFactory;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.web.util.WebUtils;

public class JwtSplitCookieService {
    public static final String JWT_HEADER_AND_PAYLOAD_COOKIE_NAME = "jwt.headerAndPayload";
    public static final String JWT_SIGNATURE_COOKIE_NAME = "jwt.signature";

    private static final String ROLES_CLAIM = "roles";
    private static final String ROLE_AUTHORITY_PREFIX = "ROLE_";

    private String issuer;

    private long expiresIn;

    private JWK jwk;

    private BearerTokenResolver bearerTokenResolver = (request -> {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        Cookie jwtHeaderAndPayload = WebUtils.getCookie(request, JWT_HEADER_AND_PAYLOAD_COOKIE_NAME);
        if (jwtHeaderAndPayload == null) {
            return null;
        }

        Cookie jwtSignature = WebUtils.getCookie(request, JWT_SIGNATURE_COOKIE_NAME);
        if (jwtSignature == null) {
            return null;
        }

        return jwtHeaderAndPayload.getValue() + "." + jwtSignature.getValue();
    });

    public JwtSplitCookieService(SecretKey secretKey, String issuer, long expiresIn) {
        this.jwk = new OctetSequenceKey.Builder(secretKey)
                .algorithm(JWSAlgorithm.parse(secretKey.getAlgorithm()))
                .build();
        this.issuer = issuer;
        this.expiresIn = expiresIn;
    }

    public BearerTokenResolver getBearerTokenResolver() {
        return bearerTokenResolver;
    }

    public void setJwtSplitCookiesIfNecessary(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws ServletException {
        final Date now = new Date();

        final List<String> roles = authentication.getAuthorities().stream().map(Objects::toString)
                .filter(a -> a.startsWith(ROLE_AUTHORITY_PREFIX)).map(a -> a.substring(ROLE_AUTHORITY_PREFIX.length()))
                .collect(Collectors.toList());

        SignedJWT signedJWT;
        try {
            JWSAlgorithm jwsAlgorithm = JWSAlgorithm.parse(jwk.getAlgorithm().getName());
            JWSHeader jwsHeader = new JWSHeader(jwsAlgorithm);

            JWSSigner signer = new DefaultJWSSignerFactory().createJWSSigner(jwk, jwsAlgorithm);
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().subject(authentication.getName()).issuer(issuer)
                    .issueTime(now).expirationTime(new Date(now.getTime() + expiresIn * 1000)).claim(ROLES_CLAIM, roles)
                    .build();
            signedJWT = new SignedJWT(jwsHeader, claimsSet);
            signedJWT.sign(signer);

            Cookie headerAndPayload = new Cookie(JWT_HEADER_AND_PAYLOAD_COOKIE_NAME,
                    new String(signedJWT.getSigningInput(), StandardCharsets.UTF_8));
            headerAndPayload.setHttpOnly(false);
            headerAndPayload.setSecure(request.isSecure());
            headerAndPayload.setPath(request.getContextPath() + "/");
            headerAndPayload.setMaxAge((int) expiresIn - 1);
            response.addCookie(headerAndPayload);

            Cookie signature = new Cookie(JWT_SIGNATURE_COOKIE_NAME, signedJWT.getSignature().toString());
            signature.setHttpOnly(true);
            signature.setSecure(request.isSecure());
            signature.setPath(request.getContextPath() + "/");
            signature.setMaxAge((int) expiresIn - 1);
            response.addCookie(signature);
        } catch (JOSEException e) {
            throw new ServletException("Unable to issue a new JWT", e);
        }
    }

    public void removeJwtSplitCookies(HttpServletRequest request, HttpServletResponse response) {
        Cookie jwtHeaderAndPayloadRemove = new Cookie(JWT_HEADER_AND_PAYLOAD_COOKIE_NAME, null);
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
