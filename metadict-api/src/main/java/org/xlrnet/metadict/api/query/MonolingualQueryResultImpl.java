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

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * Immutable implementation of {@link MonolingualQueryResult}.
 */
public class MonolingualQueryResultImpl extends AbstractQueryResult implements MonolingualQueryResult {

    private static final long serialVersionUID = -5563926572835515371L;

    private final List<MonolingualEntry> entries;

    protected MonolingualQueryResultImpl(@NotNull List<DictionaryObject> similarRecommendations, @NotNull List<ExternalContent> externalContents, @NotNull List<MonolingualEntry> entries) {
        super(similarRecommendations, externalContents);
        this.entries = entries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MonolingualQueryResultImpl)) return false;
        MonolingualQueryResultImpl that = (MonolingualQueryResultImpl) o;
        return Objects.equal(entries, that.entries);
    }

    /**
     * Returns the monolingual results of the query that match the input query. This should be used for most
     * applications
     * that involve query results.
     *
     * @return the bilingual results of the query that match the input.
     */
    @NotNull
    @Override
    public List<MonolingualEntry> getMonolingualEntries() {
        return Collections.unmodifiableList(entries);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(entries);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("entries", entries)
                .toString();
    }
}
