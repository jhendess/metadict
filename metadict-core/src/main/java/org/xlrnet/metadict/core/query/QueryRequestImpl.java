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
import org.jetbrains.annotations.NotNull;
import org.xlrnet.metadict.api.language.BilingualDictionary;
import org.xlrnet.metadict.api.language.Language;
import org.xlrnet.metadict.core.aggregation.GroupingType;
import org.xlrnet.metadict.core.aggregation.OrderType;
import org.xlrnet.metadict.core.main.MetadictCore;

import java.util.List;

/**
 * Implementation for {@link QueryRequest}.
 */
public class QueryRequestImpl implements QueryRequest {

    private final MetadictCore metadictCore;

    private final String queryString;

    private final List<BilingualDictionary> queryDictionaries;

    private final GroupingType groupingType;

    private final OrderType orderType;

    private final List<Language> monolingualLanguages;

    QueryRequestImpl(@NotNull MetadictCore metadictCore, String queryString, List<BilingualDictionary> queryDictionaries, GroupingType groupingType, OrderType orderType, List<Language> monolingualLanguages) {
        this.metadictCore = metadictCore;
        this.queryString = queryString;
        this.queryDictionaries = queryDictionaries;
        this.groupingType = groupingType;
        this.orderType = orderType;
        this.monolingualLanguages = monolingualLanguages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QueryRequestImpl)) return false;
        QueryRequestImpl that = (QueryRequestImpl) o;
        return Objects.equal(queryString, that.queryString) &&
                Objects.equal(queryDictionaries, that.queryDictionaries);
    }

    /**
     * Send this request to the Metadict core and execute it.
     *
     * @return the results of the query.
     */
    @NotNull
    @Override
    public QueryResponse executeRequest() {
        return metadictCore.executeRequest(this);
    }

    /**
     * Returns how the final query should be grouped.
     *
     * @return how the final query should be grouped.
     */
    public GroupingType getGroupingType() {
        return groupingType;
    }

    /**
     * Return a list with all dictionaries that should be queried.
     *
     * @return a list with all dictionaries that should be queried.
     */
    @Override
    @NotNull
    public List<BilingualDictionary> getBilingualDictionaries() {
        return queryDictionaries;
    }

    /**
     * Return a list of all languages that should be queried for a monolingual lookup (i.e. translations).
     *
     * @return a list of all languages that should be queried.
     */
    @NotNull
    @Override
    public List<Language> getMonolingualLanguages() {
        return monolingualLanguages;
    }

    /**
     * Returns how the final query should be grouped.
     *
     * @return how the final query should be grouped.
     */
    @NotNull
    @Override
    public GroupingType getQueryGrouping() {
        return groupingType;
    }

    /**
     * Returns how the result groups should be ordered.
     *
     * @return how the result groups should be ordered.
     */
    @NotNull
    @Override
    public OrderType getQueryOrdering() {
        return orderType;
    }

    /**
     * Returns the query string for this request. This is usually the string that will be forwarded to the search
     * backend.
     *
     * @return the query string for this request.
     */
    @Override
    @NotNull
    public String getQueryString() {
        return queryString;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(queryString, queryDictionaries);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("queryString", queryString)
                .add("queryDictionaries", queryDictionaries)
                .toString();
    }
}
