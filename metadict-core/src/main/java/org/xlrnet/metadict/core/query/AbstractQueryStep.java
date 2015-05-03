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

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.xlrnet.metadict.api.engine.SearchEngine;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Abstract base class for query steps.
 */
public class AbstractQueryStep {

    protected String searchEngineName;

    protected String queryString;

    protected SearchEngine searchEngine;

    @NotNull
    public String getQueryString() {
        return queryString;
    }

    @NotNull
    public SearchEngine getSearchEngine() {
        return searchEngine;
    }

    @NotNull
    public String getSearchEngineName() {
        return searchEngineName;
    }

    public AbstractQueryStep setQueryString(@NotNull String queryString) {
        checkNotNull(queryString);

        this.queryString = queryString;
        return this;
    }

    public AbstractQueryStep setSearchEngine(@NotNull SearchEngine searchEngine) {
        checkNotNull(searchEngine);

        this.searchEngine = searchEngine;
        return this;
    }

    @NotNull
    public AbstractQueryStep setSearchEngineName(@NotNull String searchEngineName) {
        Preconditions.checkNotNull(searchEngineName);

        this.searchEngineName = searchEngineName;
        return this;
    }
}
