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

package org.xlrnet.metadict.web.app;

import com.google.inject.servlet.RequestScoped;
import org.jetbrains.annotations.NotNull;

/**
 * Contains information about the request sent to the application.
 */
@RequestScoped
public class RequestContext {

    private String clientIdentifier;

    private String resourceId;

    /**
     * Returns the client identifier (usually IP address) of the client that performed this request.
     *
     * @return the client identifier of the client that performed this request.
     */
    @NotNull
    public String getClientIdentifier() {
        return clientIdentifier;
    }

    /**
     * Sets the client identifier of the client that performed this request.
     *
     * @param clientIdentifier
     *         the client identifier of the client that performed this request.
     */
    void setClientIdentifier(@NotNull String clientIdentifier) {
        this.clientIdentifier = clientIdentifier;
    }

    /**
     * Returns the id of the current resource.
     *
     * @return the id of the current resource.
     */
    @NotNull
    public String getResourceId() {
        return resourceId;
    }

    /**
     * Sets the id of the current resource.
     *
     * @param resourceId
     *         the client identifier of the client that performed this request.
     */
    public void setResourceId(@NotNull String resourceId) {
        this.resourceId = resourceId;
    }
}
