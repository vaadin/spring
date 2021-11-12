package com.vaadin.flow.spring.security.stateless;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimAccessor;

public class DefaultJwtClaimsSourceTest {
    static private final String TEST_USERNAME = "user";
    static private final List<String> TEST_ROLES = Arrays.asList("user",
            "employee");

    private DefaultJwtClaimsSource defaultJwtClaimsSource;

    @Mock
    private Authentication authentication;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        defaultJwtClaimsSource = new DefaultJwtClaimsSource();
    }

    @Test
    public void get_claims_sub() {
        Mockito.doReturn(TEST_USERNAME).when(authentication).getName();

        JwtClaimAccessor claimAccessor = defaultJwtClaimsSource
                .get(authentication);
        Map<String, Object> claims = claimAccessor.getClaims();
        Assert.assertEquals(TEST_USERNAME, claimAccessor.getSubject());
    }

    @Test
    public void get_claims_roles() {
        final Collection<? extends GrantedAuthority> authorities = TEST_ROLES
                .stream().map(role -> "ROLE_" + role)
                .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        Mockito.doReturn(authorities).when(authentication).getAuthorities();

        JwtClaimAccessor claimAccessor = defaultJwtClaimsSource
                .get(authentication);
        Map<String, Object> claims = claimAccessor.getClaims();
        Assert.assertEquals(TEST_ROLES,
                claimAccessor.getClaimAsStringList("roles"));
    }
}