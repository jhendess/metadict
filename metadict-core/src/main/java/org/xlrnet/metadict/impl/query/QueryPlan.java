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

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The {@link QueryPlan} is used by Metadict to determine which search engines should be used for fulfilling the
 * requested query most efficiently.
 */
public class QueryPlan {

    private List<QueryStep> queryStepList = new ArrayList<>();

    public QueryPlan addQueryStep(QueryStep queryStep) {
        queryStepList.add(queryStep);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QueryPlan)) return false;
        QueryPlan queryPlan = (QueryPlan) o;
        return Objects.equal(queryStepList, queryPlan.queryStepList);
    }

    public List<QueryStep> getQueryStepList() {
        return Collections.unmodifiableList(queryStepList);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(queryStepList);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("queryStepList", queryStepList)
                .toString();
    }
}
