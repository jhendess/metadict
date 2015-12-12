/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Jakob Hendeß
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

package org.xlrnet.metadict.web.api;

import javax.ws.rs.core.MediaType;

/**
 * Description for a single method (i.e. GET) on a REST resource.
 */
public class MethodDescription {

    private String method;

    private String fullPath;

    private MediaType[] produces;

    private MediaType[] consumes;

    public MethodDescription(String method, String fullPath, MediaType[] produces, MediaType[] consumes) {
        super();
        this.method = method;
        this.fullPath = fullPath;
        this.produces = produces;
        this.consumes = consumes;
    }

    public String getMethod() {
        return method;
    }

    public String getFullPath() {
        return fullPath;
    }

    public MediaType[] getProduces() {
        return produces;
    }

    public MediaType[] getConsumes() {
        return consumes;
    }
}
