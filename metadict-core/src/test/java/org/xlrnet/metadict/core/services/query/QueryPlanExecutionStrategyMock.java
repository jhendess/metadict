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
import org.xlrnet.metadict.core.api.query.QueryPlanExecutionStrategy;
import org.xlrnet.metadict.core.api.query.QueryStepResult;

import java.util.Collection;

/**
 * Implementation of {@link QueryPlanExecutionStrategy} which allows mocking results by passing them to the constructor.
 */
public class QueryPlanExecutionStrategyMock implements QueryPlanExecutionStrategy {

    Collection<QueryStepResult> mockedQueryResults;

    public QueryPlanExecutionStrategyMock(Collection<QueryStepResult> mockedQueryResults) {
        this.mockedQueryResults = mockedQueryResults;
    }

    /**
     * Execute the given {@link QueryPlan} with the internally provided strategy. The results of each executed {@link
     * BilingualQueryStep} have to be aggregated to a {@link Iterable} of {@link QueryStepResult} objects that
     * contains the results of each single step.
     *
     * @param queryPlan
     *         The query plan that should be executed.
     * @return a collection with the results of each step
     */
    @NotNull
    @Override
    public Collection<QueryStepResult> executeQueryPlan(@NotNull QueryPlan queryPlan) {
        return this.mockedQueryResults;
    }
}
