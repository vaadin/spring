package com.vaadin.flow.spring.security;

import com.vaadin.flow.server.VaadinSession;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

public final class VaadinAwareSecurityContextHolderStrategy
        implements SecurityContextHolderStrategy {

    private final ThreadLocal<SecurityContext> contextHolder = new ThreadLocal<>();

    @Override
    public void clearContext() {
        contextHolder.remove();
    }

    @Override
    @NonNull
    public SecurityContext getContext() {
        // We must prefer the vaadin session information over the threadlocal as
        // it is
        // more specific. It makes a huge difference if you for instance to
        // `otherSessionUI.access` in a request thread. In this case the
        // security
        // context is expected to reflect the "otherSession" and not the current
        // request.
        //
        SecurityContext context = getFromVaadinSession()
                .orElseGet(() -> contextHolder.get());
        if (context == null) {
            context = createEmptyContext();
            contextHolder.set(context);
        }
        return context;
    }

    @NonNull
    private Optional<SecurityContext> getFromVaadinSession() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session == null) {
            return Optional.empty();
        }
        Object securityContext = session.getSession().getAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
        if (securityContext instanceof SecurityContext) {
            return Optional.of((SecurityContext) securityContext);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void setContext(@NonNull SecurityContext securityContext) {
        contextHolder.set(requireNonNull(securityContext));
    }

    @Override
    @NonNull
    public SecurityContext createEmptyContext() {
        return new SecurityContextImpl();
    }
}