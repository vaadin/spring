/*
 * Copyright 2015 The original authors
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
package org.vaadin.spring.test.annotation;

import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.vaadin.spring.test.VaadinTestExecutionListener;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Place this annotation on test classes that:
 * <ul>
 * <li>are run with the {@link org.springframework.test.context.junit4.SpringJUnit4ClassRunner},</li>
 * <li>use autowiring to inject managed beans into the actual test, and</li>
 * <li>perform tests on beans that are {@link com.vaadin.spring.annotation.ViewScope view scoped}, {@link com.vaadin.spring.annotation.UIScope UI scoped} or {@link com.vaadin.spring.annotation.VaadinSessionScope session scoped}</li>
 * </ul>
 * With this annotation in place, all beans that are UI-scoped or VaadinSession-scoped will work as expected. The indented use case for this approach is
 * to test non-visual components like presenters or controllers. It is not usable for testing Vaadin components or
 * actual {@link com.vaadin.ui.UI} instances.
 * <p>
 * Example of usage:
 * <pre>
 *   &#64;RunWith(SpringJUnit4ClassRunner.class)
 *   &#64;VaadinAppConfiguration
 *   &#64;ContextConfiguration(classes = ExampleIntegrationTest.Config.class)
 *   public class MyTest {
 *
 *       &#64;Autowired MyUIScopedController myController;
 *
 *       ...
 *   }
 *   </pre>
 *
 * @author Petter Holmström (petter@vaadin.com)
 * @see org.vaadin.spring.test.VaadinScopes
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@WebAppConfiguration
@TestExecutionListeners({
        VaadinTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class
})
public @interface VaadinAppConfiguration {
}
