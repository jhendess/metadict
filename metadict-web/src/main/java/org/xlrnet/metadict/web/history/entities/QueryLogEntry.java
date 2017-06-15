/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Jakob Hendeß
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

package org.xlrnet.metadict.web.history.entities;

import org.xlrnet.metadict.core.api.query.QueryRequest;

import java.io.Serializable;
import java.time.Instant;

/**
 * Single entry of te query log. .
 */
public class QueryLogEntry implements Serializable {

    private static final long serialVersionUID = -8686903484223403622L;

    /** UUID of the query log entry. */
    private final String id;

    /** The original performed query request. */
    private final QueryRequest queryRequest;

    /** Time when the request was performed. */
    private final Instant requestTime;

    public QueryLogEntry(String id, QueryRequest queryRequest, Instant requestTime) {
        this.id = id;
        this.queryRequest = queryRequest;
        this.requestTime = requestTime;
    }

    public String getId() {
        return id;
    }

    public QueryRequest getQueryRequest() {
        return queryRequest;
    }

    public Instant getRequestTime() {
        return requestTime;
    }
}
