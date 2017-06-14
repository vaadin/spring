/*
 * Copyright 2015-2017 The original authors
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
package com.vaadin.spring.access;

import com.vaadin.navigator.View;
import com.vaadin.spring.server.SpringVaadinServletService;
import com.vaadin.ui.UI;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.context.WebApplicationContext;

import java.io.Serializable;

public abstract class AbstractSecuredViewAccessControl implements ViewAccessControl, Serializable {

	private transient WebApplicationContext webApplicationContext = null;

	@Override
	public boolean isAccessGranted(UI ui, String beanName) {
		final Secured viewSecured = getWebApplicationContext(ui).findAnnotationOnBean(beanName, Secured.class);
		return isAccessGranted(ui, viewSecured);
	}

	public boolean isAccessGranted(UI ui, Class<? extends View> viewClass) {
		Secured viewSecured = AnnotationUtils.findAnnotation(viewClass, Secured.class);
		return isAccessGranted(ui, viewSecured);
	}

	protected  boolean isAccessGranted(UI ui, Secured viewSecured) {
		if (viewSecured == null) {
			return true;
		}

		return isAccessGranted(ui, viewSecured.value());
	}

	protected abstract boolean isAccessGranted(UI ui, String securityConfigurationAttributes[]);

	protected WebApplicationContext getWebApplicationContext(UI ui) {
		if (webApplicationContext == null) {
			webApplicationContext = ((SpringVaadinServletService) ui.getSession().getService())
					.getWebApplicationContext();
		}

		return webApplicationContext;
	}
}
