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

package org.xlrnet.metadict.core.services.query;

import org.jetbrains.annotations.NotNull;
import org.xlrnet.metadict.api.query.DictionaryObject;
import org.xlrnet.metadict.api.query.ExternalContent;
import org.xlrnet.metadict.api.query.MonolingualEntry;
import org.xlrnet.metadict.api.query.SynonymEntry;
import org.xlrnet.metadict.core.api.aggregation.Group;
import org.xlrnet.metadict.core.api.aggregation.ResultEntry;
import org.xlrnet.metadict.core.api.query.QueryResponse;
import org.xlrnet.metadict.core.services.aggregation.group.GroupingType;

import java.util.Collection;

/**
 * Builder for creating new  {@link QueryResponse} objects.
 */
class QueryResponseBuilder {

    private QueryPerformanceStatistics queryPerformanceStatistics;

    private Collection<ExternalContent> externalContents;

    private Collection<Group<ResultEntry>> groupedBilingualResults;

    private Collection<DictionaryObject> similarRecommendations;

    private Collection<MonolingualEntry> monolingualEntries;

    private Collection<SynonymEntry> synonymEntries;

    private GroupingType groupingType;

    public QueryResponseBuilder setMonolingualEntries(Collection<MonolingualEntry> monolingualEntries) {
        this.monolingualEntries = monolingualEntries;
        return this;
    }

    private String queryRequestString;

    public QueryResponse build() {
        return new ImmutableQueryResponse(this.queryRequestString, this.queryPerformanceStatistics, this.externalContents, this.groupedBilingualResults, this.groupingType, this.similarRecommendations, this.monolingualEntries, this.synonymEntries);
    }

    @NotNull
    public QueryResponseBuilder setExternalContents(@NotNull Collection<ExternalContent> externalContents) {
        this.externalContents = externalContents;
        return this;
    }

    @NotNull
    public QueryResponseBuilder setGroupedBilingualResults(@NotNull Collection<Group<ResultEntry>> groupedBilingualResults) {
        this.groupedBilingualResults = groupedBilingualResults;
        return this;
    }

    @NotNull
    public QueryResponseBuilder setGroupingType(@NotNull GroupingType groupingType) {
        this.groupingType = groupingType;
        return this;
    }

    @NotNull
    public QueryResponseBuilder setQueryPerformanceStatistics(@NotNull QueryPerformanceStatistics queryPerformanceStatistics) {
        this.queryPerformanceStatistics = queryPerformanceStatistics;
        return this;
    }

    @NotNull
    public QueryResponseBuilder setQueryRequestString(@NotNull String queryRequestString) {
        this.queryRequestString = queryRequestString;
        return this;
    }

    @NotNull
    public QueryResponseBuilder setSimilarRecommendations(@NotNull Collection<DictionaryObject> similarRecommendations) {
        this.similarRecommendations = similarRecommendations;
        return this;
    }

    @NotNull
    public QueryResponseBuilder setSynonymEntries(@NotNull Collection<SynonymEntry> synonymEntries) {
        this.synonymEntries = synonymEntries;
        return this;
    }
}
