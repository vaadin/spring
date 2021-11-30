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
import org.springframework.security.oauth2.jwt.JwtClaimsSet;

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
        Mockito.doReturn(TEST_USERNAME).when(authentication).getName();
    }

    @Test
    public void get_claims_sub() {
        JwtClaimsSet claimsSet = defaultJwtClaimsSource.get(authentication);
        Assert.assertEquals(TEST_USERNAME, claimsSet.getSubject());
    }

    @Test
    public void get_claims_roles() {
        final Collection<? extends GrantedAuthority> authorities = TEST_ROLES
                .stream().map(role -> "ROLE_" + role)
                .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        Mockito.doReturn(authorities).when(authentication).getAuthorities();

        JwtClaimsSet claimsSet = defaultJwtClaimsSource.get(authentication);
        Assert.assertEquals(TEST_ROLES, claimsSet.getClaimAsStringList("roles"));
    }
}