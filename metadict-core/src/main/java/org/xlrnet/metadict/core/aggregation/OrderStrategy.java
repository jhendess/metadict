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

package org.xlrnet.metadict.core.aggregation;

import org.jetbrains.annotations.NotNull;
import org.xlrnet.metadict.core.query.QueryRequest;

import java.util.Collection;

/**
 * A {@link OrderStrategy} defines how multiple {@link ResultGroup} objects should be sorted.
 */
public interface OrderStrategy {

    /**
     * Sort the entries in the given result groups with the internal strategy and return a new collection of {@link
     * ResultGroup} objects in the specified order.
     *
     * @param queryRequest
     *         The query request that was used to create the result groups.
     * @param unorderedResultGroups
     *         An unsorted collection of result groups.
     * @return a sorted collection of groups.
     */
    @NotNull
    Collection<ResultGroup> sortResultGroups(@NotNull QueryRequest queryRequest, @NotNull Collection<ResultGroup> unorderedResultGroups);

}
