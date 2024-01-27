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
package org.vaadin.spring.events;

/**
 * Enumeration of event scopes.
 *
 * @author Petter Holmström (petter@vaadin.com)
 */
public enum EventScope {
    /**
     * The event is application wide.
     */
    APPLICATION,

    /**
     * The event is specific to the current (Vaadin) session.
     */
    SESSION,

    /**
     * The event is specific to the current UI.
     */
    UI,

    /**
     * The event is specific to the current view.
     */
    VIEW,

    /**
     * Undefined event scope. An internal event scope used only when no scope has been explicitly defined.
     *
     * @see org.vaadin.spring.events.annotation.EventBusListenerMethod#scope()
     */
    UNDEFINED
}
