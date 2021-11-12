package com.vaadin.flow.spring.security.stateless;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.SecurityContextConfigurer;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithm;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.savedrequest.CookieRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

import com.vaadin.flow.spring.security.VaadinDefaultRequestCache;

public class VaadinStatelessSecurityConfigurerTest<H extends HttpSecurityBuilder<H>> {
    static private final MacAlgorithm MAC_ALGORITHM = MacAlgorithm.HS512;
    static private final SecretKey SECRET_KEY = new SecretKeySpec(
            "testsecret".getBytes(), MAC_ALGORITHM.getName());

    private VaadinStatelessSecurityConfigurer<H> vaadinStatelessSecurityConfigurer = new VaadinStatelessSecurityConfigurer<>();

    @Mock
    private H http;

    @Mock
    private JwtSecurityContextRepository jwtSecurityContextRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void init_throws_withoutKey() {
        Assert.assertThrows(IllegalStateException.class,
                () -> vaadinStatelessSecurityConfigurer.init(http));
    }

    @Test
    public void init_uses_securityContextConfigurer() {
        vaadinStatelessSecurityConfigurer.withSecretKey().secretKey(SECRET_KEY);

        // noinspection unchecked
        SecurityContextConfigurer<H> securityContextConfigurer = (SecurityContextConfigurer<H>) Mockito
                .mock(SecurityContextConfigurer.class);
        // noinspection unchecked
        Mockito.doReturn(securityContextConfigurer).when(http)
                .getConfigurer(SecurityContextConfigurer.class);

        vaadinStatelessSecurityConfigurer.init(http);

        Mockito.verify(securityContextConfigurer).securityContextRepository(
                ArgumentMatchers.any(JwtSecurityContextRepository.class));
    }

    @Test
    public void init_sets_cookieCsrfRepository() {
        vaadinStatelessSecurityConfigurer.withSecretKey().secretKey(SECRET_KEY);

        // noinspection unchecked
        CsrfConfigurer<H> csrfConfigurer = (CsrfConfigurer<H>) Mockito
                .mock(CsrfConfigurer.class);
        // noinspection unchecked
        Mockito.doReturn(csrfConfigurer).when(http)
                .getConfigurer(CsrfConfigurer.class);

        vaadinStatelessSecurityConfigurer.init(http);

        ArgumentCaptor<CsrfTokenRepository> csrfTokenRepositoryArgumentCaptor = ArgumentCaptor
                .forClass(CsrfTokenRepository.class);
        Mockito.verify(csrfConfigurer).csrfTokenRepository(
                csrfTokenRepositoryArgumentCaptor.capture());
        CsrfTokenRepository csrfTokenRepository = csrfTokenRepositoryArgumentCaptor
                .getValue();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Mockito.doReturn(response).when(request)
                .getAttribute(HttpServletResponse.class.getName());
        Mockito.doReturn("/context-path").when(request).getContextPath();
        CsrfToken csrfToken = csrfTokenRepository.generateToken(request);
        csrfTokenRepository.saveToken(csrfToken, request, response);
        String csrfTokenValue = csrfToken.getToken();
        ArgumentCaptor<Cookie> cookieArgumentCaptor = ArgumentCaptor
                .forClass(Cookie.class);
        Mockito.verify(response).addCookie(cookieArgumentCaptor.capture());
        Cookie tokenCookie = cookieArgumentCaptor.getValue();
        Assert.assertNotNull(tokenCookie);
        Assert.assertEquals("XSRF-TOKEN", tokenCookie.getName());
        Assert.assertNotNull(csrfTokenValue);
        Assert.assertEquals(csrfTokenValue, tokenCookie.getValue());
    }

    @Test
    public void withSecretKey_customizerAndCarryAreEquivalent() {
        AtomicReference<VaadinStatelessSecurityConfigurer<H>.SecretKeyConfigurer> ref = new AtomicReference<>();

        vaadinStatelessSecurityConfigurer.withSecretKey(ref::set);

        VaadinStatelessSecurityConfigurer<H>.SecretKeyConfigurer secretKeyConfigurer = vaadinStatelessSecurityConfigurer
                .withSecretKey();

        Assert.assertEquals(secretKeyConfigurer, ref.get());

        vaadinStatelessSecurityConfigurer.withSecretKey(ref::set);

        Assert.assertEquals(secretKeyConfigurer, ref.get());

        secretKeyConfigurer = secretKeyConfigurer.secretKey(SECRET_KEY);

        Assert.assertEquals(ref.get(), secretKeyConfigurer);

        secretKeyConfigurer = secretKeyConfigurer.algorithm(MAC_ALGORITHM);
        Assert.assertEquals(ref.get(), secretKeyConfigurer);

        Assert.assertEquals(vaadinStatelessSecurityConfigurer,
                secretKeyConfigurer.and());
    }

    @Test
    public void withSecretKey_emptyKey() {
        vaadinStatelessSecurityConfigurer.withSecretKey();

        vaadinStatelessSecurityConfigurer.init(http);

        Mockito.verify(http).setSharedObject(
                ArgumentMatchers.eq(SecurityContextRepository.class),
                ArgumentMatchers.any(JwtSecurityContextRepository.class));

        vaadinStatelessSecurityConfigurer.configure(http);

        assertNoSecretKeyAndNoAlgorithm();
    }

    @Test
    public void withSecretKey_emptyKey_algorithm() {
        vaadinStatelessSecurityConfigurer.withSecretKey()
                .algorithm(MAC_ALGORITHM);

        vaadinStatelessSecurityConfigurer.init(http);

        Mockito.verify(http).setSharedObject(
                ArgumentMatchers.eq(SecurityContextRepository.class),
                ArgumentMatchers.any(JwtSecurityContextRepository.class));

        vaadinStatelessSecurityConfigurer.configure(http);

        assertNoSecretKeyAndNoAlgorithm();
    }

    @Test
    public void withSecretKey_secretKey() {
        vaadinStatelessSecurityConfigurer.withSecretKey().secretKey(SECRET_KEY);

        vaadinStatelessSecurityConfigurer.init(http);

        Mockito.verify(http).setSharedObject(
                ArgumentMatchers.eq(SecurityContextRepository.class),
                ArgumentMatchers.any(JwtSecurityContextRepository.class));

        Mockito.doReturn(jwtSecurityContextRepository).when(http)
                .getSharedObject(SecurityContextRepository.class);

        vaadinStatelessSecurityConfigurer.configure(http);

        assertSecretKeyAndAlgorithm(SECRET_KEY, MAC_ALGORITHM);
    }

    @Test
    public void withSecretKey_algorithm() {
        MacAlgorithm customAlgorithm = MacAlgorithm.HS384;
        vaadinStatelessSecurityConfigurer.withSecretKey().secretKey(SECRET_KEY)
                .algorithm(customAlgorithm);

        Mockito.doReturn(jwtSecurityContextRepository).when(http)
                .getSharedObject(SecurityContextRepository.class);

        vaadinStatelessSecurityConfigurer.configure(http);

        assertSecretKeyAndAlgorithm(SECRET_KEY, customAlgorithm);
    }

    @Test
    public void configure_issuer() {
        String issuer = "com.example.test";
        vaadinStatelessSecurityConfigurer.withSecretKey().secretKey(SECRET_KEY)
                .and().issuer(issuer);

        Mockito.doReturn(jwtSecurityContextRepository).when(http)
                .getSharedObject(SecurityContextRepository.class);

        vaadinStatelessSecurityConfigurer.configure(http);

        Mockito.verify(jwtSecurityContextRepository).setIssuer(issuer);
    }

    @Test
    public void configure_expiresIn() {
        long expiresIn = 180;
        vaadinStatelessSecurityConfigurer.withSecretKey().secretKey(SECRET_KEY)
                .and().expiresIn(expiresIn);

        Mockito.doReturn(jwtSecurityContextRepository).when(http)
                .getSharedObject(SecurityContextRepository.class);

        vaadinStatelessSecurityConfigurer.configure(http);

        Mockito.verify(jwtSecurityContextRepository).setExpiresIn(expiresIn);
    }

    @Test
    public void configure_jwtClaimsSource() {
        JwtClaimsSource jwtClaimsSource = authentication -> Jwt
                .withTokenValue("jwt").build();
        vaadinStatelessSecurityConfigurer.withSecretKey().secretKey(SECRET_KEY)
                .and().jwtClaimsSource(jwtClaimsSource);

        Mockito.doReturn(jwtSecurityContextRepository).when(http)
                .getSharedObject(SecurityContextRepository.class);

        vaadinStatelessSecurityConfigurer.configure(http);

        Mockito.verify(jwtSecurityContextRepository)
                .setJwtClaimsSource(jwtClaimsSource);
    }

    @Test
    public void configure_setsCookieRequestCache_withVaadinDefaultRequestCache() {
        VaadinDefaultRequestCache vaadinDefaultRequestCache = Mockito
                .mock(VaadinDefaultRequestCache.class);
        Mockito.doReturn(vaadinDefaultRequestCache).when(http)
                .getSharedObject(RequestCache.class);

        vaadinStatelessSecurityConfigurer.configure(http);

        ArgumentCaptor<RequestCache> requestCacheArgumentCaptor = ArgumentCaptor
                .forClass(RequestCache.class);
        Mockito.verify(vaadinDefaultRequestCache)
                .setDelegateRequestCache(requestCacheArgumentCaptor.capture());
        Assert.assertTrue(requestCacheArgumentCaptor
                .getValue() instanceof CookieRequestCache);
    }

    private void assertNoSecretKeyAndNoAlgorithm() {
        Mockito.verify(jwtSecurityContextRepository, Mockito.never())
                .setJwkSource(Mockito.any());
        Mockito.verify(jwtSecurityContextRepository, Mockito.never())
                .setJwsAlgorithm(Mockito.any());
    }

    private void assertSecretKeyAndAlgorithm(SecretKey expectedSecretKey,
            JwsAlgorithm expectedAlgorithm) {
        ArgumentCaptor<JWSAlgorithm> jwsAlgorithmArgumentCaptor = ArgumentCaptor
                .forClass(JWSAlgorithm.class);
        Mockito.verify(jwtSecurityContextRepository)
                .setJwsAlgorithm(jwsAlgorithmArgumentCaptor.capture());
        Assert.assertEquals(jwsAlgorithmArgumentCaptor.getValue().getName(),
                expectedAlgorithm.getName());
        // noinspection unchecked
        ArgumentCaptor<JWKSource<SecurityContext>> jwkSourceArgumentCaptor = ArgumentCaptor
                .forClass(JWKSource.class);
        Mockito.verify(jwtSecurityContextRepository)
                .setJwkSource(jwkSourceArgumentCaptor.capture());
        List<JWK> jwkList = ((ImmutableJWKSet<SecurityContext>) jwkSourceArgumentCaptor
                .getValue()).getJWKSet().getKeys();
        Assert.assertEquals(1, jwkList.size());
        OctetSequenceKey octetSequenceKey = jwkList.get(0).toOctetSequenceKey();
        Assert.assertArrayEquals(expectedSecretKey.getEncoded(),
                octetSequenceKey.toByteArray());
        Assert.assertEquals(expectedAlgorithm.getName(),
                octetSequenceKey.getAlgorithm().getName());
    }
}