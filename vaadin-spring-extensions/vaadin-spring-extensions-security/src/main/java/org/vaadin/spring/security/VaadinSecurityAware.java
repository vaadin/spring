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
package org.vaadin.spring.security;

import org.springframework.beans.factory.Aware;

/**
 * Interface to be implemented by any object that wishes to be notified
 * of the {@link org.vaadin.spring.security.VaadinSecurity}.
 *
 * @author Gert-Jan Timmer (gjr.timmer@gmail.com)
 */
public interface VaadinSecurityAware extends Aware {

    /**
     * Set the VaadinSecurity.
     * <p>Invoked after population of normal bean properties but before an init callback such
     * as {@link org.springframework.beans.factory.InitializingBean#afterPropertiesSet()}
     * or a custom init-method.
     *
     * @param vaadinSecurity the VaadinSecurity object used within the applicationContext.
     */
    void setVaadinSecurity(VaadinSecurity vaadinSecurity);
}
