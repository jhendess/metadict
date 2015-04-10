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

package org.xlrnet.metadict.impl.query;

import org.xlrnet.metadict.api.query.DictionaryObject;
import org.xlrnet.metadict.api.query.ExternalContent;
import org.xlrnet.metadict.impl.aggregation.GroupingType;
import org.xlrnet.metadict.impl.aggregation.ResultGroup;

import java.util.Collection;

/**
 * Builder for creating new  {@link QueryResponse} objects.
 */
class QueryResponseBuilder {

    private QueryPerformanceStatistics queryPerformanceStatistics;

    private Collection<ExternalContent> externalContents;

    private Collection<ResultGroup> groupedResults;

    private GroupingType groupingType;

    private Collection<DictionaryObject> similarRecommendations;

    private String queryRequestString;

    public QueryResponse build() {
        return new QueryResponseImpl(queryRequestString, queryPerformanceStatistics, externalContents, groupedResults, groupingType, similarRecommendations);
    }

    public QueryResponseBuilder setExternalContents(Collection<ExternalContent> externalContents) {
        this.externalContents = externalContents;
        return this;
    }

    public QueryResponseBuilder setGroupedResults(Collection<ResultGroup> groupedResults) {
        this.groupedResults = groupedResults;
        return this;
    }

    public QueryResponseBuilder setGroupingType(GroupingType groupingType) {
        this.groupingType = groupingType;
        return this;
    }

    public QueryResponseBuilder setQueryPerformanceStatistics(QueryPerformanceStatistics queryPerformanceStatistics) {
        this.queryPerformanceStatistics = queryPerformanceStatistics;
        return this;
    }

    public QueryResponseBuilder setQueryRequestString(String queryRequestString) {
        this.queryRequestString = queryRequestString;
        return this;
    }

    public QueryResponseBuilder setSimilarRecommendations(Collection<DictionaryObject> similarRecommendations) {
        this.similarRecommendations = similarRecommendations;
        return this;
    }
}
