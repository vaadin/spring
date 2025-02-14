/*
 * Copyright 2015-2025 The original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vaadin.spring.internal;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import org.junit.After;
import org.junit.Test;
import org.springframework.mock.web.MockHttpSession;

import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.WrappedHttpSession;
import com.vaadin.server.WrappedSession;

/**
 * Test case for {@link VaadinSessionScope}, originally introduced to detect a
 * memory leak (https://github.com/vaadin/spring/issues/243).
 */
public class VaadinSessionScopeTest {

    @Test
    public void testMemoryLeakWhenCleaningUpVaadinSessionScope() {
        // Set up mocks
        VaadinService mockService = mock(VaadinService.class);
        WrappedSession mockWrappedSession = new WrappedHttpSession(
                new MockHttpSession());

        // Set up spy
        VaadinSession sessionSpy = spy(new VaadinSession(mockService));
        doReturn(true).when(sessionSpy).hasLock();
        doNothing().when(sessionSpy).lock();
        doNothing().when(sessionSpy).unlock();
        doReturn(VaadinSession.State.OPEN).when(sessionSpy).getState();
        doReturn(mockWrappedSession).when(sessionSpy).getSession();
        VaadinSession.setCurrent(sessionSpy);

        // Run test
        BeanStore beanStore = VaadinSessionScope.getBeanStoreRetrievalStrategy()
                .getBeanStore();
        assertSame("BeanStore is stored in session", beanStore,
                sessionSpy.getAttribute(BeanStore.class));

        VaadinSessionScope.cleanupSession(sessionSpy);
        assertNull("BeanStore has been removed from session",
                sessionSpy.getAttribute(BeanStore.class));
    }

    @After
    public void teardown() {
        VaadinSession.setCurrent(null);
    }
}
