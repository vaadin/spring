package com.vaadin.flow.spring.security;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.flow.server.HandlerHelper;
import com.vaadin.flow.server.connect.EndpointUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * Provides basic Vaadin security configuration for the project.
 * <p>
 * Sets up security rules for a Vaadin application and restricts all URLs except
 * for public resources and internal Vaadin URLs to authenticated user.
 * <p>
 * The default behavior can be altered by extending the public/protected methods
 * in the class.
 * <p>
 * To use this, create your own web security configurer adapter class by
 * extending this class instead of <code>WebSecurityConfigurerAdapter</code> and
 * annotate it with <code>@EnableWebSecurity</code> and
 * <code>@Configuration</code>.
 * <p>
 * For example <code>
&#64;EnableWebSecurity
&#64;Configuration
public class MySecurityConfigurerAdapter extends VaadinWebSecurityConfigurerAdapter {
    
} 
 * </code>
 * 
 */
public abstract class VaadinWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    @Autowired
    private VaadinDefaultRequestCache vaadinDefaultRequestCache;

    @Autowired
    private EndpointUtil endpointUtil;

    @Autowired
    private RequestUtil requestUtil;

    /**
     * The paths listed as "ignoring" in this method are handled without any Spring
     * Security involvement. They have no access to any security context etc.
     * <p>
     * {@inheritDoc}
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().requestMatchers(getDefaultWebSecurityIgnoreMatcher());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Vaadin has its own CSRF protection.
        // Spring CSRF is not compatible with Vaadin internal requests
        http.csrf().ignoringRequestMatchers(requestUtil::isFrameworkInternalRequest);
        // nor with endpoints
        http.csrf().ignoringRequestMatchers(endpointUtil::isEndpointRequest);

        // Ensure automated requests to e.g. closing push channels, service workers,
        // endpoints are not counted as valid targets to redirect user to on login
        http.requestCache().requestCache(vaadinDefaultRequestCache);

        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry urlRegistry = http
                .authorizeRequests();
        urlRegistry.requestMatchers(getDefaultHttpSecurityPermitMatcher()).permitAll();

        // all other requests require authentication
        urlRegistry.anyRequest().authenticated();
    }

    /**
     * Matcher for app shell (index page) and framework internal requests.
     *
     * @return default {@link HttpSecurity} bypass matcher
     */
    private RequestMatcher getDefaultHttpSecurityPermitMatcher() {
        List<RequestMatcher> matchers = new ArrayList<>();
        for (String publicResource : HandlerHelper.getPublicResourcesRequiringSecurityContext()) {
            matchers.add(new AntPathRequestMatcher(publicResource));
        }

        matchers.add(new AntPathRequestMatcher("/vaadinServlet/**"));

        // Vaadin internal requests must always be allowed to allow public Flow pages
        // and/or login page implemented using Flow.
        matchers.add(requestUtil::isFrameworkInternalRequest);

        return new OrRequestMatcher(matchers);
    }

    /**
     * Matcher for Vaadin static resources.
     *
     * @return default {@link WebSecurity} ignore matcher
     */
    private static RequestMatcher getDefaultWebSecurityIgnoreMatcher() {
        return new OrRequestMatcher(Stream.of(HandlerHelper.getPublicResources()).map(AntPathRequestMatcher::new)
                .collect(Collectors.toList()));
    }

}
