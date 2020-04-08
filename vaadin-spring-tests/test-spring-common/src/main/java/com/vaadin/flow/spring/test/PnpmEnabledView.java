/*
 * Copyright 2000-2020 Vaadin Ltd.
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
package com.vaadin.flow.spring.test;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.Constants;

@Route("pnpm-enabled")
public class PnpmEnabledView extends Div {

    @Autowired
    private Environment env;

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        boolean isPnpm = new File("node_modules/.modules.yaml").exists();
        String enableProperty = env.getProperty(
                "vaadin." + Constants.SERVLET_PARAMETER_ENABLE_PNPM);
        DeploymentConfiguration configuration = getUI().get().getSession()
                .getService().getDeploymentConfiguration();
        Div checkPnpm = new Div();
        checkPnpm.setId("check-pnpm");
        checkPnpm.setText(
                String.valueOf(Boolean.TRUE.toString().equals(enableProperty)
                        && !configuration.isProductionMode()));
        Div pnpm = new Div();
        pnpm.setId("pnpm");
        pnpm.setText(String.valueOf(isPnpm));

        add(checkPnpm, pnpm);
    }
}
