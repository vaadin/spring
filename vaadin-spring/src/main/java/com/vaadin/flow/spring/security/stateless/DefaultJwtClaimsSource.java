/*
 * Copyright 2000-2021 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.flow.spring.security.stateless;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimAccessor;

/**
 * A {@link JwtClaimsSource} implementation that creates a set with the {@code
 * "sub"} and {@code "roles"} JWT claims using the authentication principal name
 * and roles respectively.
 */
public class DefaultJwtClaimsSource implements JwtClaimsSource {
    static final String ROLES_CLAIM = "roles";
    static final String ROLE_AUTHORITY_PREFIX = "ROLE_";

    @Override
    public JwtClaimAccessor get(Authentication authentication) {
        final List<String> roles = authentication.getAuthorities().stream()
                .map(Objects::toString)
                .filter(a -> a.startsWith(ROLE_AUTHORITY_PREFIX))
                .map(a -> a.substring(ROLE_AUTHORITY_PREFIX.length()))
                .collect(Collectors.toList());

        return Jwt.withTokenValue("jwt").header("type", "jwt")
                .subject(authentication.getName()).claim(ROLES_CLAIM, roles)
                .build();
    }
}
