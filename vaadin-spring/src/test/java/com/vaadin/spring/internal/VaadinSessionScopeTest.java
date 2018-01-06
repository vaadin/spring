package com.vaadin.spring.internal;

import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.WrappedHttpSession;
import com.vaadin.server.WrappedSession;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpSession;

/**
 * Test case for {@link VaadinSessionScope}, originally introduced to detect a
 * memory leak (https://github.com/vaadin/spring/issues/243).
 */
public class VaadinSessionScopeTest {

    @Test
    public void testMemoryLeakWhenCleaningUpVaadinSessionScope() {
        // Set up mocks
        VaadinService mockService = Mockito.mock(VaadinService.class);
        WrappedSession mockWrappedSession = new WrappedHttpSession(new MockHttpSession());

        // Set up spy
        VaadinSession sessionSpy = Mockito.spy(new VaadinSession(mockService));
        Mockito.doReturn(true).when(sessionSpy).hasLock();
        Mockito.doNothing().when(sessionSpy).lock();
        Mockito.doNothing().when(sessionSpy).unlock();
        Mockito.doReturn(VaadinSession.State.OPEN).when(sessionSpy).getState();
        Mockito.doReturn(mockWrappedSession).when(sessionSpy).getSession();
        VaadinSession.setCurrent(sessionSpy);

        // Run test
        BeanStore beanStore = VaadinSessionScope.getBeanStoreRetrievalStrategy().getBeanStore();
        Assert.assertSame("BeanStore is stored in session", beanStore, sessionSpy.getAttribute(BeanStore.class));

        VaadinSessionScope.cleanupSession(sessionSpy);
        Assert.assertNull("BeanStore has been removed from session", sessionSpy.getAttribute(BeanStore.class));
    }
}
