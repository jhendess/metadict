/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Jakob Hendeß
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

package org.xlrnet.metadict.core.api.query;

import org.jetbrains.annotations.NotNull;
import org.xlrnet.metadict.core.services.query.BilingualQueryStep;
import org.xlrnet.metadict.core.services.query.QueryPlan;

import java.util.Collection;

/**
 * The {@link QueryPlanExecutionStrategy} interface is used for implementing query plan executors. A query plan
 * executor has to execute every step defined in a {@link QueryPlan}. However, the executor can decide how the query
 * plan gets executed by e.g. implementing multi-threading or smart caching.
 */
public interface QueryPlanExecutionStrategy {

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
    Collection<QueryStepResult> executeQueryPlan(@NotNull QueryPlan queryPlan);

}
