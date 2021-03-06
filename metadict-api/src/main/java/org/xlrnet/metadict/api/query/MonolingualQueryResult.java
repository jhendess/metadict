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

package org.xlrnet.metadict.api.query;

import org.jetbrains.annotations.NotNull;
import org.xlrnet.metadict.api.language.Language;

import java.util.List;

/**
 * The interface {@link MonolingualQueryResult} represents a collection of {@link MonolingualEntry} objects. This
 * interface should always be used as the return type from the search method {@link
 * org.xlrnet.metadict.api.engine.SearchEngine#executeMonolingualQuery(String, Language)}. To build a new instance, you
 * can use the {@link MonolingualQueryResultBuilder}.
 */
public interface MonolingualQueryResult extends EngineQueryResult {

    /**
     * Returns the monolingual results of the query that match the input query. This should be used for most applications
     * that involve query results.
     *
     * @return the bilingual results of the query that match the input.
     */
    @NotNull
    List<MonolingualEntry> getMonolingualEntries();

}
