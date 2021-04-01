package com.vaadin.flow.spring.security;

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vaadin.flow.server.HandlerHelper.RequestType;
import com.vaadin.flow.server.connect.EndpointUtil;
import com.vaadin.flow.server.connect.VaadinEndpointProperties;
import com.vaadin.flow.shared.ApplicationConstants;
import com.vaadin.flow.spring.VaadinConfigurationProperties;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { VaadinDefaultRequestCache.class, RequestUtil.class, EndpointUtil.class,
        VaadinEndpointProperties.class, VaadinConfigurationProperties.class })
public class VaadinDefaultRequestCacheTest {

    @Autowired
    VaadinDefaultRequestCache cache;
    @Autowired
    RequestUtil requestUtil;
    @Autowired
    EndpointUtil endpointUtil;

    @Test
    public void normalRouteRequestSaved() {
        HttpServletRequest request = createRequest("/hello-world", null);
        HttpServletResponse response = createResponse();

        Assert.assertNull(cache.getRequest(request, response));
        cache.saveRequest(request, response);
        Assert.assertNotNull(cache.getRequest(request, response));
    }

    @Test
    public void internalRequestsNotSaved() {
        HttpServletRequest request = createRequest(null, RequestType.INIT);
        HttpServletResponse response = createResponse();
        Assert.assertTrue(requestUtil.isFrameworkInternalRequest(request));
        cache.saveRequest(request, response);
        Assert.assertNull(cache.getRequest(request, response));
    }

    @Test
    public void serviceWorkerRequestNotSaved() {
        HttpServletRequest request = createRequest("", null,
                Collections.singletonMap("Referer", "https://labs.vaadin.com/business/sw.js"));
        HttpServletResponse response = createResponse();
        Assert.assertFalse(requestUtil.isFrameworkInternalRequest(request));
        cache.saveRequest(request, response);
        Assert.assertNull(cache.getRequest(request, response));
    }

    @Test
    public void endpointRequestNotSaved() {
        HttpServletRequest request = createRequest("/connect/MyClass/MyEndpoint", null);
        HttpServletResponse response = createResponse();
        Assert.assertTrue(endpointUtil.isEndpointRequest(request));
        cache.saveRequest(request, response);
        Assert.assertNull(cache.getRequest(request, response));
    }

    private HttpServletRequest createRequest(String pathInfo, RequestType type) {
        return createRequest(pathInfo, type, Collections.emptyMap());
    }

    private HttpServletRequest createRequest(String pathInfo, RequestType type, Map<String, String> headers) {
        String uri = "http://localhost:8080" + (pathInfo == null ? "/" : pathInfo);
        MockHttpServletRequest r = new MockHttpServletRequest("GET", uri);
        r.setPathInfo(pathInfo);
        if (type != null) {
            r.setParameter(ApplicationConstants.REQUEST_TYPE_PARAMETER, type.getIdentifier());
        }
        headers.forEach((key, value) -> r.addHeader(key, value));

        return r;
    }

    private HttpServletResponse createResponse() {
        return Mockito.mock(HttpServletResponse.class);
    }

}
