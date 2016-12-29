/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Jakob Hendeß
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.xlrnet.metadict.web.middleware.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.web.app.RequestContext;
import org.xlrnet.metadict.web.services.RateControlService;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * Generic filter which provides a generic rate limit on the metadict REST API.
 */
@Provider
public class RateLimitFilter implements ContainerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RateLimitFilter.class);

    /**
     * HTTP code for too many requests.
     */
    private static final int HTTP_TOO_MANY_REQUESTS = 429;

    private final com.google.inject.Provider<RequestContext> requestContextProvider;

    private final RateControlService rateControlService;

    @Inject
    public RateLimitFilter(com.google.inject.Provider<RequestContext> requestContextProvider, RateControlService rateControlService) {
        this.requestContextProvider = requestContextProvider;
        this.rateControlService = rateControlService;
    }

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        RequestContext requestContext = requestContextProvider.get();
        boolean isAllowed = rateControlService.checkRateLimit(requestContext);
        if (!isAllowed) {
            LOGGER.info("Too many requests from {} to resource {}", requestContext.getClientIdentifier(), requestContext.getResourceId());
            containerRequestContext.abortWith(Response.status(HTTP_TOO_MANY_REQUESTS).build());
        }
    }
}
