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

import java.util.List;

/**
 * Immutable implementation for {@link BilingualQueryResult}.
 */
public class ImmutableBilingualQueryResult extends AbstractQueryResult implements BilingualQueryResult {

    public static final BilingualQueryResult EMPTY_QUERY_RESULT = new BilingualQueryResultBuilder().build();

    protected final List<BilingualEntry> entries;

    /**
     * Create a new immutable instance. See {@link BilingualQueryResult} for more information about the parameters.
     *
     * @param entries
     *         The entries to add.
     * @param similarRecommendations
     *         The similarity recommendations.
     * @param externalContents
     *         Ta set of external contents.
     */
    ImmutableBilingualQueryResult(List<BilingualEntry> entries, List<DictionaryObject> similarRecommendations, List<ExternalContent> externalContents) {
        super(similarRecommendations, externalContents);
        this.entries = entries;
    }

    /**
     * Return a new builder instance for creating new {@link BilingualQueryResult} objects.
     *
     * @return a new builder.
     */
    public static BilingualQueryResultBuilder builder() {
        return new BilingualQueryResultBuilder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImmutableBilingualQueryResult)) return false;
        ImmutableBilingualQueryResult that = (ImmutableBilingualQueryResult) o;
        return Objects.equal(entries, that.entries) &&
                Objects.equal(similarRecommendations, that.similarRecommendations) &&
                Objects.equal(externalContents, that.externalContents);
    }

    /**
     * Returns the bilingual (i.e translations) results of the query that match the input. This should be used for most applications that involve
     * query results.
     *
     * @return the results of the query that match the input.
     */
    @NotNull
    @Override
    public List<BilingualEntry> getBilingualEntries() {
        return entries;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(entries, similarRecommendations, externalContents);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("entries", entries)
                .add("similarRecommendations", super.getSimilarRecommendations())
                .add("externalContents", super.getExternalContents())
                .toString();
    }
}
