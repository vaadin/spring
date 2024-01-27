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
package org.vaadin.spring.http;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implementation of {@link org.vaadin.spring.http.HttpService}.
 * 
 * @author Marko Radinovic (markoradinovic79@gmail.com)
 * @author Gert-Jan Timmer (gjr.timmer@gmail.com)
 * @see <a href="https://github.com/markoradinovic/Vaadin4Spring-MVP-Sample-SpringSecurity">Original code</a>
 */
public class VaadinHttpService implements HttpService {

    @Autowired
    private HttpServletRequest request;

    @Resource(name = HttpResponseFactory.BEAN_NAME)
    private HttpServletResponse response;

    @Override
    public HttpServletRequest getCurrentRequest() {		
        return request;
    }

    @Override
    public HttpServletResponse getCurrentResponse() {
        return response;
    }

}
