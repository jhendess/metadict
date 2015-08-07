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

import java.util.Collections;
import java.util.List;

/**
 * Abstract class for implementing various query result types.
 */
public class AbstractQueryResult implements EngineQueryResult {

    private static final long serialVersionUID = -2265934528706550873L;

    protected final List<DictionaryObject> similarRecommendations;

    protected final List<ExternalContent> externalContents;

    protected final List<SynonymEntry> synonymEntries;

    protected AbstractQueryResult(@NotNull List<DictionaryObject> similarRecommendations, @NotNull List<ExternalContent> externalContents, @NotNull List<SynonymEntry> synonymEntries) {
        this.similarRecommendations = similarRecommendations;
        this.externalContents = externalContents;
        this.synonymEntries = synonymEntries;
    }

    /**
     * Returns all collected external content for the query. This can be used to provide links to relevant blog posts
     * or forum entries.
     *
     * @return all collected external content for the query.
     */
    @NotNull
    public List<ExternalContent> getExternalContents() {
        return Collections.unmodifiableList(externalContents);
    }

    /**
     * Returns additional recommendations for the user. The recommendations have to implement only {@link
     * DictionaryObject}. A whole translation with two languages is therefore not needed.
     *
     * @return additional recommendations for the user.
     */
    @NotNull
    public List<DictionaryObject> getSimilarRecommendations() {
        return Collections.unmodifiableList(similarRecommendations);
    }

    /**
     * Returns all synonyms in this result set. Each {@link SynonymEntry} contains different synonyms for exactly one
     * base word.
     *
     * @return all synonyms in this result set
     */
    @NotNull
    public List<SynonymEntry> getSynonymEntries() {
        return synonymEntries;
    }
}
