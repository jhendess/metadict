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

import com.google.common.base.MoreObjects;
import com.google.common.collect.Iterables;
import org.xlrnet.metadict.api.query.DictionaryObject;
import org.xlrnet.metadict.api.query.ExternalContent;
import org.xlrnet.metadict.api.query.MonolingualEntry;
import org.xlrnet.metadict.core.aggregation.GroupingType;
import org.xlrnet.metadict.core.aggregation.ResultEntry;
import org.xlrnet.metadict.core.aggregation.ResultGroup;

import java.util.Collection;
import java.util.Collections;

/**
 * Implementation for {@link QueryResponse},
 */
public class QueryResponseImpl implements QueryResponse {

    private final QueryPerformanceStatistics queryPerformanceStatistics;

    private final Collection<ExternalContent> externalContents;

    private final Collection<ResultGroup> groupedResults;

    private final GroupingType groupingType;

    private final Collection<DictionaryObject> similarRecommendations;

    private final String requestString;

    private final Collection<MonolingualEntry> monolingualEntries;

    QueryResponseImpl(String requestString, QueryPerformanceStatistics queryPerformanceStatistics, Collection<ExternalContent> externalContents, Collection<ResultGroup> groupedResults, GroupingType groupingType, Collection<DictionaryObject> similarRecommendations, Collection<MonolingualEntry> monolingualEntries) {
        this.requestString = requestString;
        this.queryPerformanceStatistics = queryPerformanceStatistics;
        this.externalContents = externalContents;
        this.groupedResults = groupedResults;
        this.groupingType = groupingType;
        this.similarRecommendations = similarRecommendations;
        this.monolingualEntries = monolingualEntries;
    }

    /**
     * Returns a {@link Collection} with all available {@link ExternalContent} objects from the query.
     *
     * @return all available {@link ExternalContent} objects from the query.
     */
    @Override
    public Collection<ExternalContent> getExternalContents() {
        if (this.externalContents != null)
            return Collections.unmodifiableCollection(this.externalContents);
        return Collections.EMPTY_LIST;
    }

    /**
     * Returns a view on the underlying result set based on the requested grouping mechanism. Each element of {@link
     * ResultGroup} in the returned collection contains the {@link ResultEntry} objects that were matched to this
     * group.
     *
     * @return a view on the underlying result set based on the requested grouping mechanism.
     */
    @Override
    public Collection<ResultGroup> getGroupedBilingualResults() {
        if (this.groupedResults != null)
            return Collections.unmodifiableCollection(this.groupedResults);
        return Collections.EMPTY_LIST;
    }

    /**
     * Returns the {@link GroupingType} that was used for grouping the resulting set.
     *
     * @return the {@link GroupingType} that was used for grouping the resulting set.
     */
    @Override
    public GroupingType getGroupingType() {
        return this.groupingType;
    }

    /**
     * Returns a {@link Collection} of monolingual entries in the result sets. Monolingual entries are currently
     * <i>not</i> grouped.
     *
     * @return a {@link Collection} of monolingual entries in the result sets.
     */
    @Override
    public Collection<MonolingualEntry> getMonolingualEntries() {
        if (this.monolingualEntries != null)
            return Collections.unmodifiableCollection(this.monolingualEntries);
        return Collections.EMPTY_LIST;
    }

    /**
     * Returns internal performance information about the query.
     *
     * @return internal performance information about the query.
     */
    @Override
    public QueryPerformanceStatistics getPerformanceStatistics() {
        return this.queryPerformanceStatistics;
    }

    /**
     * Returns the original input query.
     *
     * @return the original input query.
     */
    @Override
    public String getRequestString() {
        return this.requestString;
    }

    /**
     * Returns a {@link Collection} with additional search recommendations for the user. Search recommendations are
     * only available in one language (i.e. no translation provided).
     *
     * @return a collection with additional search recommendations for the user.
     */
    @Override
    public Collection<DictionaryObject> getSimilarRecommendations() {
        if (this.similarRecommendations != null)
            return Collections.unmodifiableCollection(this.similarRecommendations);
        return Collections.EMPTY_LIST;
    }

    /**
     * Returns an {@link Iterable} that can be used to iterate over all {@link ResultEntry} objects of the query. Each
     * {@link ResultEntry} object that can be obtained via {@link ResultGroup} must also be accessible through the
     * returned iterator of this method.
     *
     * @return an {@link Iterable} that can be used to iterate over all {@link ResultEntry} objects of the query.
     */
    @Override
    public Iterable<ResultEntry> getUngroupedBilingualResults() {
        return Iterables.concat(getGroupedBilingualResults());       // Don't access field directly to avoid NPEs!
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("queryPerformanceStatistics", queryPerformanceStatistics)
                .add("externalContents", externalContents)
                .add("groupedResults", groupedResults)
                .add("groupingType", groupingType)
                .add("similarRecommendations", similarRecommendations)
                .add("requestString", requestString)
                .toString();
    }
}
