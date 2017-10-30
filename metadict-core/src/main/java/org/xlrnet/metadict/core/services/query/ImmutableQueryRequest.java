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

import com.google.common.base.Objects;
import org.jetbrains.annotations.NotNull;
import org.xlrnet.metadict.api.language.BilingualDictionary;
import org.xlrnet.metadict.api.language.Language;
import org.xlrnet.metadict.core.api.query.QueryRequest;
import org.xlrnet.metadict.core.services.aggregation.group.GroupingType;
import org.xlrnet.metadict.core.services.aggregation.order.OrderType;

import java.io.Serializable;
import java.util.List;

/**
 * Implementation for {@link QueryRequest}.
 */
public class ImmutableQueryRequest implements QueryRequest, Serializable {

    private static final long serialVersionUID = -8365716036210376923L;

    private final String queryString;

    private final String originalQueryString;

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

    ImmutableQueryRequest(String queryString, List<BilingualDictionary> queryDictionaries, GroupingType groupingType, OrderType orderType, List<Language> monolingualLanguages, String originalQueryString) {
        this.queryString = queryString;
        this.queryDictionaries = queryDictionaries;
        this.groupingType = groupingType;
        this.orderType = orderType;
        this.monolingualLanguages = monolingualLanguages;
        this.originalQueryString = originalQueryString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImmutableQueryRequest)) return false;
        ImmutableQueryRequest that = (ImmutableQueryRequest) o;
        return Objects.equal(this.queryString, that.queryString) &&
                Objects.equal(this.queryDictionaries, that.queryDictionaries);
    }

    @NotNull
    public GroupingType getGroupingType() {
        return this.groupingType;
    }

    @Override
    @NotNull
    public List<BilingualDictionary> getBilingualDictionaries() {
        return this.queryDictionaries;
    }

    @NotNull
    @Override
    public List<Language> getMonolingualLanguages() {
        return this.monolingualLanguages;
    }

    @NotNull
    @Override
    public GroupingType getQueryGrouping() {
        return this.groupingType;
    }

    @NotNull
    @Override
    public OrderType getQueryOrdering() {
        return this.orderType;
    }

    @Override
    @NotNull
    public String getQueryString() {
        return this.queryString;
    }

    @Override
    @NotNull
    public String getOriginalQueryString() {
        return originalQueryString;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.queryString, this.queryDictionaries);
    }

}
