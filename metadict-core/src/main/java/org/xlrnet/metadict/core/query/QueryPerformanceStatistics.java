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

/**
 * The class {@link QueryPerformanceStatistics} provides information about how long the internal processing of a query
 * took. All durations are given in milliseconds.
 */
public class QueryPerformanceStatistics {

    long totalDuration;

    long planningPhaseDuration;

    long queryPhaseDuration;

    long groupPhaseDuration;

    long orderPhaseDuration;

    long collectPhaseDuration;

    public long getCollectPhaseDuration() {
        return collectPhaseDuration;
    }

    protected QueryPerformanceStatistics setCollectPhaseDuration(long collectPhaseDuration) {
        this.collectPhaseDuration = collectPhaseDuration;
        return this;
    }

    public long getGroupPhaseDuration() {
        return groupPhaseDuration;
    }

    protected QueryPerformanceStatistics setGroupPhaseDuration(long groupPhaseDuration) {
        this.groupPhaseDuration = groupPhaseDuration;
        return this;
    }

    public long getOrderPhaseDuration() {
        return orderPhaseDuration;
    }

    protected QueryPerformanceStatistics setOrderPhaseDuration(long orderPhaseDuration) {
        this.orderPhaseDuration = orderPhaseDuration;
        return this;
    }

    public long getPlanningPhaseDuration() {
        return planningPhaseDuration;
    }

    protected QueryPerformanceStatistics setPlanningPhaseDuration(long planningPhaseDuration) {
        this.planningPhaseDuration = planningPhaseDuration;
        return this;
    }

    public long getQueryPhaseDuration() {
        return queryPhaseDuration;
    }

    protected QueryPerformanceStatistics setQueryPhaseDuration(long queryPhaseDuration) {
        this.queryPhaseDuration = queryPhaseDuration;
        return this;
    }

    public long getTotalDuration() {
        return totalDuration;
    }

    protected QueryPerformanceStatistics setTotalDuration(long totalDuration) {
        this.totalDuration = totalDuration;
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("totalDuration", totalDuration)
                .add("planningPhaseDuration", planningPhaseDuration)
                .add("queryPhaseDuration", queryPhaseDuration)
                .add("groupPhaseDuration", groupPhaseDuration)
                .add("orderPhaseDuration", orderPhaseDuration)
                .add("collectPhaseDuration", collectPhaseDuration)
                .toString();
    }
}
