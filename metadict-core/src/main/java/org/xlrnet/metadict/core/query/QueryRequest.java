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

package org.xlrnet.metadict.core.query;

import org.jetbrains.annotations.NotNull;
import org.xlrnet.metadict.api.language.Dictionary;
import org.xlrnet.metadict.core.aggregation.GroupingType;
import org.xlrnet.metadict.core.aggregation.OrderType;
import org.xlrnet.metadict.core.exception.MetadictException;

import java.util.List;

/**
 * The {@link QueryRequest} class represents a single query request,that can be sent to the metadict core.
 */
public interface QueryRequest {

    /**
     * Send this request to the Metadict core and execute it.
     *
     * @return the results of the query.
     * @throws MetadictException
     *         Will be thrown if the query fails.
     */
    @NotNull
    QueryResponse executeRequest();

    /**
     * Return a list with all dictionaries that should be queried.
     *
     * @return a list with all dictionaries that should be queried.
     */
    @NotNull
    List<Dictionary> getQueryDictionaries();

    /**
     * Returns how the final query should be grouped.
     *
     * @return how the final query should be grouped.
     */
    @NotNull
    GroupingType getQueryGrouping();

    /**
     * Returns how the result groups should be ordered.
     *
     * @return how the result groups should be ordered.
     */
    @NotNull
    OrderType getQueryOrdering();

    /**
     * Returns the query string for this request. This is usually the string that will be forwarded to the search
     * backend.
     *
     * @return the query string for this request.
     */
    @NotNull
    String getQueryString();
}
