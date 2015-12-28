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
import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import org.xlrnet.metadict.api.query.*;
import org.xlrnet.metadict.core.aggregation.GroupingType;
import org.xlrnet.metadict.core.aggregation.ResultEntry;
import org.xlrnet.metadict.core.aggregation.ResultGroup;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

/**
 * Implementation for {@link QueryResponse},
 */
public class ImmutableQueryResponse implements QueryResponse, Serializable {

    private static final long serialVersionUID = 5254033672770609246L;

    private final QueryPerformanceStatistics queryPerformanceStatistics;

    private final Collection<ExternalContent> externalContents;

    private final Collection<ResultGroup> groupedBilingualResults;

    private final GroupingType groupingType;

    private final Collection<DictionaryObject> similarRecommendations;

    private final String requestString;

    private final Collection<MonolingualEntry> monolingualEntries;

    private final Collection<SynonymEntry> synonymEntries;

    ImmutableQueryResponse(String requestString, QueryPerformanceStatistics queryPerformanceStatistics, Collection<ExternalContent> externalContents, Collection<ResultGroup> groupedBilingualResults, GroupingType groupingType, Collection<DictionaryObject> similarRecommendations, Collection<MonolingualEntry> monolingualEntries, Collection<SynonymEntry> synonymEntries) {
        this.requestString = requestString;
        this.queryPerformanceStatistics = queryPerformanceStatistics;
        this.externalContents = externalContents;
        this.groupedBilingualResults = groupedBilingualResults;
        this.groupingType = groupingType;
        this.similarRecommendations = similarRecommendations;
        this.monolingualEntries = monolingualEntries;
        this.synonymEntries = synonymEntries;
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
    public Collection<ResultGroup> getGroupedBilingualEntries() {
        if (this.groupedBilingualResults != null)
            return Collections.unmodifiableCollection(this.groupedBilingualResults);
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
     * Returns a monolingual set of synonym entries for single objects. Each entry
     * represents all synonyms for a certain object (word, phrase, etc.) where the synonyms are grouped into
     * different{@link SynonymGroup} objects.
     *
     * @return a monolingual set of synonym entries for single objects.
     */
    @Override
    public Collection<SynonymEntry> getSynonymEntries() {
        return synonymEntries;
    }

    /**
     * Returns an {@link Iterable} that can be used to iterate over all {@link ResultEntry} objects of the query. Each
     * {@link ResultEntry} object that can be obtained via {@link ResultGroup} must also be accessible through the
     * returned iterator of this method.
     *
     * @return an {@link Iterable} that can be used to iterate over all {@link ResultEntry} objects of the query.
     */
    @Override
    public Iterable<ResultEntry> getUngroupedBilingualEntries() {
        return Iterables.concat(getGroupedBilingualEntries());       // Don't access field directly to avoid NPEs!
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("queryPerformanceStatistics", queryPerformanceStatistics)
                .add("externalContents", externalContents)
                .add("groupedBilingualResults", groupedBilingualResults)
                .add("groupingType", groupingType)
                .add("similarRecommendations", similarRecommendations)
                .add("requestString", requestString)
                .add("monolingualEntries", monolingualEntries)
                .add("synonymEntries", synonymEntries)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImmutableQueryResponse)) return false;
        ImmutableQueryResponse that = (ImmutableQueryResponse) o;
        return Objects.equal(queryPerformanceStatistics, that.queryPerformanceStatistics) &&
                Objects.equal(externalContents, that.externalContents) &&
                Objects.equal(groupedBilingualResults, that.groupedBilingualResults) &&
                Objects.equal(groupingType, that.groupingType) &&
                Objects.equal(similarRecommendations, that.similarRecommendations) &&
                Objects.equal(requestString, that.requestString) &&
                Objects.equal(monolingualEntries, that.monolingualEntries) &&
                Objects.equal(synonymEntries, that.synonymEntries);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(queryPerformanceStatistics, externalContents, groupedBilingualResults, groupingType, similarRecommendations, requestString, monolingualEntries, synonymEntries);
    }
}
