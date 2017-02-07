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

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import org.xlrnet.metadict.api.query.DictionaryObject;
import org.xlrnet.metadict.api.query.ExternalContent;
import org.xlrnet.metadict.api.query.MonolingualEntry;
import org.xlrnet.metadict.api.query.SynonymEntry;
import org.xlrnet.metadict.core.api.aggregation.Group;
import org.xlrnet.metadict.core.api.aggregation.ResultEntry;
import org.xlrnet.metadict.core.api.query.QueryResponse;
import org.xlrnet.metadict.core.services.aggregation.group.GroupingType;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

/**
 * Implementation for {@link QueryResponse},
 */
public class ImmutableQueryResponse implements QueryResponse, Serializable {

    private static final long serialVersionUID = -6886895807189117173L;

    private final QueryPerformanceStatistics queryPerformanceStatistics;

    private final Collection<ExternalContent> externalContents;

    private final Collection<Group<ResultEntry>> groupedBilingualResults;

    private final GroupingType groupingType;

    private final Collection<DictionaryObject> similarRecommendations;

    private final String requestString;

    private final Collection<MonolingualEntry> monolingualEntries;

    private final Collection<SynonymEntry> synonymEntries;

    ImmutableQueryResponse(String requestString, QueryPerformanceStatistics queryPerformanceStatistics, Collection<ExternalContent> externalContents, Collection<Group<ResultEntry>> groupedBilingualResults, GroupingType groupingType, Collection<DictionaryObject> similarRecommendations, Collection<MonolingualEntry> monolingualEntries, Collection<SynonymEntry> synonymEntries) {
        this.requestString = requestString;
        this.queryPerformanceStatistics = queryPerformanceStatistics;
        this.externalContents = externalContents;
        this.groupedBilingualResults = groupedBilingualResults;
        this.groupingType = groupingType;
        this.similarRecommendations = similarRecommendations;
        this.monolingualEntries = monolingualEntries;
        this.synonymEntries = synonymEntries;
    }

    @Override
    public Collection<ExternalContent> getExternalContents() {
        if (this.externalContents != null) {
            return Collections.unmodifiableCollection(this.externalContents);
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public Collection<Group<ResultEntry>> getGroupedBilingualEntries() {
        if (this.groupedBilingualResults != null) {
            return Collections.unmodifiableCollection(this.groupedBilingualResults);
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public GroupingType getGroupingType() {
        return this.groupingType;
    }

    @Override
    public Collection<MonolingualEntry> getMonolingualEntries() {
        if (this.monolingualEntries != null) {
            return Collections.unmodifiableCollection(this.monolingualEntries);
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public QueryPerformanceStatistics getPerformanceStatistics() {
        return this.queryPerformanceStatistics;
    }


    @Override
    public String getRequestString() {
        return this.requestString;
    }

    @Override
    public Collection<DictionaryObject> getSimilarRecommendations() {
        if (this.similarRecommendations != null) {
            return Collections.unmodifiableCollection(this.similarRecommendations);
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public Collection<SynonymEntry> getSynonymEntries() {
        return this.synonymEntries;
    }

    @Override
    public Iterable<ResultEntry> getUngroupedBilingualEntries() {
        return Iterables.concat(getGroupedBilingualEntries());       // Don't access field directly to avoid NPEs!
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("queryPerformanceStatistics", this.queryPerformanceStatistics)
                .add("externalContents", this.externalContents)
                .add("groupedBilingualResults", this.groupedBilingualResults)
                .add("groupingType", this.groupingType)
                .add("similarRecommendations", this.similarRecommendations)
                .add("requestString", this.requestString)
                .add("monolingualEntries", this.monolingualEntries)
                .add("synonymEntries", this.synonymEntries)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImmutableQueryResponse)) return false;
        ImmutableQueryResponse that = (ImmutableQueryResponse) o;
        return Objects.equal(this.queryPerformanceStatistics, that.queryPerformanceStatistics) &&
                Objects.equal(this.externalContents, that.externalContents) &&
                Objects.equal(this.groupedBilingualResults, that.groupedBilingualResults) &&
                Objects.equal(this.groupingType, that.groupingType) &&
                Objects.equal(this.similarRecommendations, that.similarRecommendations) &&
                Objects.equal(this.requestString, that.requestString) &&
                Objects.equal(this.monolingualEntries, that.monolingualEntries) &&
                Objects.equal(this.synonymEntries, that.synonymEntries);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.queryPerformanceStatistics, this.externalContents, this.groupedBilingualResults, this.groupingType, this.similarRecommendations, this.requestString, this.monolingualEntries, this.synonymEntries);
    }
}
