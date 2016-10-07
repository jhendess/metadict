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

import com.google.common.base.Objects;
import org.jetbrains.annotations.NotNull;
import org.xlrnet.metadict.api.language.BilingualDictionary;
import org.xlrnet.metadict.api.language.Language;
import org.xlrnet.metadict.core.aggregation.GroupingType;
import org.xlrnet.metadict.core.aggregation.OrderType;

import java.util.List;

/**
 * Implementation for {@link QueryRequest}.
 */
public class ImmutableQueryRequest implements QueryRequest {

    private final String queryString;

    @Override
    public String toString() {
        return "QueryRequestImpl{" +
                "queryString='" + this.queryString + '\'' +
                ", queryDictionaries=" + this.queryDictionaries +
                ", groupingType=" + this.groupingType +
                ", orderType=" + this.orderType +
                ", monolingualLanguages=" + this.monolingualLanguages +
                '}';
    }

    private final List<BilingualDictionary> queryDictionaries;

    private final GroupingType groupingType;

    private final OrderType orderType;

    private final List<Language> monolingualLanguages;

    ImmutableQueryRequest(String queryString, List<BilingualDictionary> queryDictionaries, GroupingType groupingType, OrderType orderType, List<Language> monolingualLanguages) {
        this.queryString = queryString;
        this.queryDictionaries = queryDictionaries;
        this.groupingType = groupingType;
        this.orderType = orderType;
        this.monolingualLanguages = monolingualLanguages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImmutableQueryRequest)) return false;
        ImmutableQueryRequest that = (ImmutableQueryRequest) o;
        return Objects.equal(this.queryString, that.queryString) &&
                Objects.equal(this.queryDictionaries, that.queryDictionaries);
    }

    /**
     * Returns how the final query should be grouped.
     *
     * @return how the final query should be grouped.
     */
    @NotNull
    public GroupingType getGroupingType() {
        return this.groupingType;
    }

    /**
     * Return a list with all dictionaries that should be queried.
     *
     * @return a list with all dictionaries that should be queried.
     */
    @Override
    @NotNull
    public List<BilingualDictionary> getBilingualDictionaries() {
        return this.queryDictionaries;
    }

    /**
     * Return a list of all languages that should be queried for a monolingual lookup (i.e. translations).
     *
     * @return a list of all languages that should be queried.
     */
    @NotNull
    @Override
    public List<Language> getMonolingualLanguages() {
        return this.monolingualLanguages;
    }

    /**
     * Returns how the final query should be grouped.
     *
     * @return how the final query should be grouped.
     */
    @NotNull
    @Override
    public GroupingType getQueryGrouping() {
        return this.groupingType;
    }

    /**
     * Returns how the result groups should be ordered.
     *
     * @return how the result groups should be ordered.
     */
    @NotNull
    @Override
    public OrderType getQueryOrdering() {
        return this.orderType;
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
        return this.queryString;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.queryString, this.queryDictionaries);
    }

}
