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

package org.xlrnet.metadict.web.middleware.app;


import com.google.inject.servlet.RequestScoped;
import org.apache.commons.lang3.StringUtils;
import org.xlrnet.metadict.api.auth.User;
import org.xlrnet.metadict.web.auth.services.UserService;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.SecurityContext;
import java.util.Optional;


/**
 * Creates a new {@link RequestContext} object for the active request.
 */
@RequestScoped
public class RequestContextProvider implements Provider<RequestContext> {

    private final HttpServletRequest request;


    private final UserService userService;

    @Inject
    public RequestContextProvider(HttpServletRequest request, SecurityContext securityContext, UserService userService) {
        this.request = request;
        this.userService = userService;
    }

    /**
     * Creates a {@link RequestContext} for the current request.
     *
     * @return An initialized {@link RequestContext}.
     */
    @Override
    public RequestContext get() {
        String clientIdentifier = request.getRemoteAddr();
        String pathInfo = request.getPathInfo();
        String resourceId = StringUtils.countMatches(pathInfo, "/") > 1 ? StringUtils.substring(pathInfo, 0, pathInfo.indexOf("/", 1)) : pathInfo;
        resourceId = StringUtils.substringAfter(resourceId, "/");
        Optional<User> user = Optional.empty();
        return new RequestContext(clientIdentifier, resourceId, user.orElse(null));
    }
}
