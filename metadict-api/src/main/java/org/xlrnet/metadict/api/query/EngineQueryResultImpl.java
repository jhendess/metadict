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

import com.google.common.base.Objects;

import java.util.List;

/**
 * Immutable implementation for {@link EngineQueryResult}.
 */
public class EngineQueryResultImpl implements EngineQueryResult {

    protected final List<DictionaryEntry> entries;

    protected final List<DictionaryObject> similarRecommendations;

    protected final List<ExternalContent> externalContents;

    /**
     * Create a new immutable instance. See {@link EngineQueryResult} for more information about the parameters.
     *
     * @param entries
     *         The entries to add.
     * @param similarRecommendations
     *         The similarity recommendations.
     * @param externalContents
     *         Ta set of external contents.
     */
    EngineQueryResultImpl(List<DictionaryEntry> entries, List<DictionaryObject> similarRecommendations, List<ExternalContent> externalContents) {
        this.entries = entries;
        this.similarRecommendations = similarRecommendations;
        this.externalContents = externalContents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EngineQueryResultImpl)) return false;
        EngineQueryResultImpl that = (EngineQueryResultImpl) o;
        return Objects.equal(entries, that.entries) &&
                Objects.equal(similarRecommendations, that.similarRecommendations) &&
                Objects.equal(externalContents, that.externalContents);
    }

    /**
     * Returns the results of the query that match the input. This should be used for most applications that involve
     * query results.
     *
     * @return the results of the query that match the input.
     */
    @Override
    public List<DictionaryEntry> getEntries() {
        return entries;
    }

    /**
     * Returns all collected external content for the query. This can be used to provide links to relevant blog posts
     * or forum entries.
     *
     * @return all collected external content for the query.
     */
    @Override
    public List<ExternalContent> getExternalContents() {
        return externalContents;
    }

    /**
     * Returns additional recommendations for the user. The recommendations have to implement only {@link
     * DictionaryObject}. A whole translation with two languages is therefore not needed.
     *
     * @return additional recommendations for the user.
     */
    @Override
    public List<DictionaryObject> getSimilarRecommendations() {
        return similarRecommendations;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(entries, similarRecommendations, externalContents);
    }
}
