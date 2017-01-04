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

package org.xlrnet.metadict.web.services;

import com.google.common.util.concurrent.RateLimiter;
import org.xlrnet.metadict.web.app.RequestContext;

import javax.inject.Singleton;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton service for controlling and limiting the amount of calls made to Metadict.
 */
@Singleton
public class RateControlService {

    private static final double CALLS_PER_SECOND = 4.0;     // FIXME: Just temporarily increased for ITs

    private final ConcurrentHashMap<String, RateLimiter> rateLimiterMap = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, RateLimiter> rateLimiterMapLong = new ConcurrentHashMap<>();

    /**
     * Checks if the client belonging to the given {@link RequestContext} has exaggerated the rate limit for a given
     * resource id. If the client has not yet exaggerated the limit, this method will return true. However, if the
     * client has performed more request in the past timeframe than allowed, false will be returned.
     *
     * @param requestContext
     *         The client's request context.
     * @param resourceId
     *         Any resource id for which the rate limit should be verified.
     */
    public boolean checkRateLimit(RequestContext requestContext) {
        String clientSpecificRequestKey = requestContext.getClientIdentifier() + "_+_" + requestContext.getResourceId();
        // TODO: Refactor this if-construct to a cleaner solution
        RateLimiter rateLimiter;
        if ("session".equals(requestContext.getResourceId())) {
            rateLimiterMapLong.computeIfAbsent(clientSpecificRequestKey, (c) -> RateLimiter.create(10 * CALLS_PER_SECOND));
            rateLimiter = rateLimiterMapLong.get(clientSpecificRequestKey);
        } else {
            rateLimiterMap.computeIfAbsent(clientSpecificRequestKey, (c) -> RateLimiter.create(CALLS_PER_SECOND));
            rateLimiter = rateLimiterMap.get(clientSpecificRequestKey);
        }
        return rateLimiter.tryAcquire();
    }
}
