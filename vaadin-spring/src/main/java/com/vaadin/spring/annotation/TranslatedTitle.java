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
package com.vaadin.spring.annotation;

import com.vaadin.ui.UI;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines the HTML page title for a {@link UI}, that will be translated by
 * {@link org.springframework.context.MessageSource}.
 *
 * This annotation should be used instead of {@link com.vaadin.annotations.Title}. {@link com.vaadin.annotations.Title}
 * is always prefered before {@code TranslatedTitle} to guarantee the backwards compatibility.
 *
 * @author Benno Müller (benno.mueller@saxess-ag.de)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TranslatedTitle {

    /**
     * Gets the HTML title as translatable message code that should be used if the UI is used on it's own.
     *
     * @return a message code for the page title string
     */
    String key();

    /**
     * Gets the default message for the page title string, that is taken if there is no message for the
     * {@link #key() message key} available.
     *
     * The fallback default value  is "?[key]?"
     *
     * @return default message for the page title string
     */
    String defaultValue();

}
