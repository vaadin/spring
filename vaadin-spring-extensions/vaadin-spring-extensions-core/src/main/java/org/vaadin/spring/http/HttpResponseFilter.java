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

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filter to provide access to the {@link HttpServletResponse}
 * 
 * @author Marko Radinovic (markoradinovic79@gmail.com)
 * @author Gert-Jan Timmer (gjr.timmer@gmail.com)
 * @see <a href="https://github.com/markoradinovic/Vaadin4Spring-MVP-Sample-SpringSecurity">Original code</a>
 */
public class HttpResponseFilter implements Filter {

    private ThreadLocal<HttpServletResponse> responses = new ThreadLocal<HttpServletResponse>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        try {
            HttpServletResponse r = (HttpServletResponse) response;
            responses.set(r);
            chain.doFilter(request, response);
        } finally {
            responses.remove();
        }		
    }

    public HttpServletResponse getHttpServletResponse() {
        return responses.get();
    }

    @Override
    public void destroy() {		

    }

}
