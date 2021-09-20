package com.vaadin.flow.spring.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;

class JwtSecurityContextRepository implements SecurityContextRepository {
    private static final String ROLES_CLAIM = "roles";
    private static final String ROLE_AUTHORITY_PREFIX = "ROLE_";
    private final Log logger = LogFactory.getLog(this.getClass());
    private String issuer;
    private long expiresIn = 1800L;
    private JWKSource<com.nimbusds.jose.proc.SecurityContext> jwkSource;
    private JWSAlgorithm jwsAlgorithm;
    private JwtDecoder jwtDecoder;
    private AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();
    final private SerializedJwtSplitCookieRepository serializedJwtSplitCookieRepository = new SerializedJwtSplitCookieRepository();
    final private JwtAuthenticationConverter jwtAuthenticationConverter;

    JwtSecurityContextRepository() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix(ROLE_AUTHORITY_PREFIX);
        grantedAuthoritiesConverter.setAuthoritiesClaimName(ROLES_CLAIM);

        jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
                grantedAuthoritiesConverter);
    }

    void setJwkSource(
            JWKSource<com.nimbusds.jose.proc.SecurityContext> jwkSource) {
        this.jwkSource = jwkSource;
    }

    void setJwsAlgorithm(JWSAlgorithm jwsAlgorithm) {
        this.jwsAlgorithm = jwsAlgorithm;
    }

    void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
        this.serializedJwtSplitCookieRepository.setExpiresIn(expiresIn);
    }

    void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public void setTrustResolver(AuthenticationTrustResolver trustResolver) {
        this.trustResolver = trustResolver;
    }

    private JwtDecoder getJwtDecoder() {
        if (jwtDecoder != null) {
            return jwtDecoder;
        }


        DefaultJWTProcessor<com.nimbusds.jose.proc.SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
        jwtProcessor.setJWTClaimsSetVerifier((claimsSet, context) -> {
        });

        JWSKeySelector<com.nimbusds.jose.proc.SecurityContext> jwsKeySelector = new JWSVerificationKeySelector<com.nimbusds.jose.proc.SecurityContext>(
                jwsAlgorithm, jwkSource);
        jwtProcessor.setJWSKeySelector(jwsKeySelector);
        NimbusJwtDecoder jwtDecoder = new NimbusJwtDecoder(jwtProcessor);
        jwtDecoder.setJwtValidator(
                issuer != null ? JwtValidators.createDefaultWithIssuer(issuer)
                        : JwtValidators.createDefault());
        this.jwtDecoder = jwtDecoder;
        return jwtDecoder;
    }

    private String encodeJwt(HttpServletRequest request,
            HttpServletResponse response, Authentication authentication)
            throws JOSEException {
        if (authentication == null ||
                trustResolver.isAnonymous(authentication)) {
            return null;
        }

        final Date now = new Date();

        final List<String> roles = authentication.getAuthorities().stream()
                .map(Objects::toString)
                .filter(a -> a.startsWith(ROLE_AUTHORITY_PREFIX))
                .map(a -> a.substring(ROLE_AUTHORITY_PREFIX.length()))
                .collect(Collectors.toList());

        SignedJWT signedJWT;
        JWSHeader jwsHeader = new JWSHeader(jwsAlgorithm);
        JWKSelector jwkSelector = new JWKSelector(
                JWKMatcher.forJWSHeader(jwsHeader));

        List<JWK> jwks = jwkSource.get(jwkSelector, null);
        JWK jwk = jwks.get(0);

        JWSSigner signer = new DefaultJWSSignerFactory().createJWSSigner(jwk,
                jwsAlgorithm);
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().subject(
                        authentication.getName()).issuer(issuer).issueTime(now)
                .expirationTime(new Date(now.getTime() + expiresIn * 1000))
                .claim(ROLES_CLAIM, roles).build();
        signedJWT = new SignedJWT(jwsHeader, claimsSet);
        signedJWT.sign(signer);

        return signedJWT.serialize();
    }

    @Override
    public SecurityContext loadContext(
            HttpRequestResponseHolder requestResponseHolder) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        String serializedJwt = serializedJwtSplitCookieRepository.loadSerializedJwt(
                requestResponseHolder.getRequest());
        Jwt jwt = getJwtDecoder().decode(serializedJwt);
        if (jwt != null) {
            Authentication authentication = jwtAuthenticationConverter.convert(
                    jwt);
            context.setAuthentication(authentication);
        }

        return context;
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request,
            HttpServletResponse response) {
        String serializedJwt;
        try {
            serializedJwt = encodeJwt(request, response,
                    context.getAuthentication());
        } catch (JOSEException e) {
            logger.warn("Cannot serialize SecurityContext as JWT", e);
            serializedJwt = null;
        }
        serializedJwtSplitCookieRepository.saveSerializedJwt(serializedJwt,
                request, response);
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        return serializedJwtSplitCookieRepository.containsSerializedJwt(
                request);
    }
}
