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

package org.xlrnet.metadict.core.strategies;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.UncheckedExecutionException;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.api.language.Language;
import org.xlrnet.metadict.api.query.EngineQueryResult;
import org.xlrnet.metadict.api.query.EngineQueryResultBuilder;
import org.xlrnet.metadict.core.query.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Execution strategy that executes each query consecutively but uses an internal data structure for caching the
 * results of each query.
 */
@DefaultExecutionStrategy
public class CachedLinearExecutionStrategy implements QueryPlanExecutionStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(CachedLinearExecutionStrategy.class);

    Cache<QueryStep, QueryStepResult> queryStepResultCache = CacheBuilder
            .newBuilder()
            .concurrencyLevel(8)
            .initialCapacity(512)
            .maximumSize(8192)
            .build();

    /**
     * Execute the given {@link QueryPlan} with the internally provided strategy. The results of each executed {@link
     * QueryStep} have to be aggregated to a {@link Iterable< Pair <QueryStep, EngineQueryResult >>} that
     * contains the results of each single step.
     *
     * @param queryPlan
     *         The query plan that should be executed. The caller of this method has make sure that the provided query
     *         plan is valid.
     * @return a collection with the results of each step
     */
    @NotNull
    @Override
    public Collection<QueryStepResult> executeQueryPlan(@NotNull QueryPlan queryPlan) {
        List<QueryStepResult> queryResults = new ArrayList<>();

        for (QueryStep currentQueryStep : queryPlan.getQueryStepList()) {
            QueryStepResult queryStepResult = queryStepResultCache.getIfPresent(currentQueryStep);

            try {
                if (queryStepResult == null) {
                    LOGGER.debug("Cache miss on query step {}", currentQueryStep);
                    queryStepResult = queryStepResultCache.get(currentQueryStep, () -> executeQueryStep(currentQueryStep));
                }
            } catch (ExecutionException | UncheckedExecutionException e) {
                LOGGER.error("Query step {} failed", currentQueryStep, e);
                queryStepResult = new QueryStepResultBuilder()
                        .setFailedStep(true)
                        .setQueryStep(currentQueryStep)
                        .setErrorMessage(e.getMessage())
                        .setEngineQueryResult(EngineQueryResultBuilder.EMPTY_QUERY_RESULT)
                        .build();
            }
            if (queryStepResult != null && queryStepResult.isFailedStep())
                queryStepResultCache.invalidate(currentQueryStep);
            queryResults.add(queryStepResult);
        }

        return queryResults;
    }

    @NotNull
    QueryStepResult executeQueryStep(QueryStep step) {
        LOGGER.debug("Executing query step {}", step);

        QueryStepResultBuilder stepResultBuilder = new QueryStepResultBuilder().setQueryStep(step);
        long startTime = System.currentTimeMillis();

        try {
            String queryString = step.getQueryString();
            Language inputLanguage = step.getInputLanguage();
            Language outLanguage = step.getOutputLanguage();
            boolean allowBothWay = step.isAllowBothWay();

            EngineQueryResult queryResult = step.getSearchEngine().executeSearchQuery(queryString, inputLanguage, outLanguage, allowBothWay);

            if (queryResult == null) {
                LOGGER.error("Query step {} failed: query result was null", step);
                stepResultBuilder.setFailedStep(true).setErrorMessage("query result was null")
                        .setExecutionTime(System.currentTimeMillis() - startTime);
                queryResult = EngineQueryResultBuilder.EMPTY_QUERY_RESULT;
            }
            stepResultBuilder.setEngineQueryResult(queryResult);

            long executionTime = System.currentTimeMillis() - startTime;
            stepResultBuilder.setExecutionTime(executionTime);

            LOGGER.debug("Executed query step {} in {} ms", step, executionTime);

        } catch (Exception e) {
            LOGGER.error("Query step {} failed", step, e);
            stepResultBuilder.setFailedStep(true).setErrorMessage(e.getMessage())
                    .setEngineQueryResult(EngineQueryResultBuilder.EMPTY_QUERY_RESULT)
                    .setExecutionTime(System.currentTimeMillis() - startTime);
        }
        return stepResultBuilder.build();
    }
}
