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
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.api.language.Language;
import org.xlrnet.metadict.api.query.BilingualQueryResult;
import org.xlrnet.metadict.api.query.EngineQueryResult;
import org.xlrnet.metadict.api.query.ImmutableBilingualQueryResult;
import org.xlrnet.metadict.api.query.MonolingualQueryResult;
import org.xlrnet.metadict.api.storage.StorageBackendException;
import org.xlrnet.metadict.api.storage.StorageOperationException;
import org.xlrnet.metadict.api.storage.StorageService;
import org.xlrnet.metadict.core.query.*;
import org.xlrnet.metadict.core.storage.DefaultStorageService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Execution strategy that executes each query consecutively but uses an internal data structure for caching the
 * results of each query.
 */
@DefaultExecutionStrategy
public class CachedLinearExecutionStrategy implements QueryPlanExecutionStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(CachedLinearExecutionStrategy.class);

    private final StorageService storageService;

    @Inject
    public CachedLinearExecutionStrategy(@DefaultStorageService StorageService storageService) {
        this.storageService = storageService;
    }

    Cache<AbstractQueryStep, QueryStepResult> queryStepResultCache = CacheBuilder
            .newBuilder()
            .concurrencyLevel(8)
            .initialCapacity(512)
            .maximumSize(8192)
            .build();

    @NotNull
    @Override
    public Collection<QueryStepResult> executeQueryPlan(@NotNull QueryPlan queryPlan) {
        List<QueryStepResult> queryResults = new ArrayList<>();

        for (AbstractQueryStep currentQueryStep : queryPlan.getQueryStepList()) {
            QueryStepResult queryStepResult = this.queryStepResultCache.getIfPresent(currentQueryStep);

            try {
                if (queryStepResult == null) {
                    LOGGER.debug("Local cache miss on query step {}", currentQueryStep);
                    queryStepResult = this.queryStepResultCache.get(currentQueryStep, () -> accessStorageService(currentQueryStep));
                } else {
                    LOGGER.debug("Local cache hit on query step {}", currentQueryStep);
                }
            } catch (ExecutionException | UncheckedExecutionException e) {
                LOGGER.error("Query step {} failed", currentQueryStep, e);
                queryStepResult = new QueryStepResultBuilder()
                        .setFailedStep(true)
                        .setQueryStep(currentQueryStep)
                        .setErrorMessage(e.getMessage())
                        .setEngineQueryResult(ImmutableBilingualQueryResult.EMPTY_QUERY_RESULT)
                        .build();
            }
            if (queryStepResult != null && queryStepResult.isFailedStep())
                this.queryStepResultCache.invalidate(currentQueryStep);
            queryResults.add(queryStepResult);
        }

        return queryResults;
    }

    private QueryStepResult accessStorageService(AbstractQueryStep currentQueryStep) {
        String queryStepKey = currentQueryStep.toString();
        Optional<QueryStepResult> storedStepResult = this.storageService.read("QueryCache", queryStepKey, QueryStepResult.class);

        if (storedStepResult.isPresent())
            return storedStepResult.get();

        QueryStepResult queryStepResult = executeQueryStep(currentQueryStep);

        // Do not store the result if it failed to avoid cache pollution
        if (queryStepResult.isFailedStep())
            return queryStepResult;

        try {
            this.storageService.create("QueryCache", queryStepKey, queryStepResult);
        } catch (StorageBackendException b) {
            LOGGER.error("Internal storage backend error", b);
        } catch (StorageOperationException o) {
            LOGGER.warn("Storage backend was updated before results could be created - using own result");
        }

        return queryStepResult;
    }

    @NotNull
    QueryStepResult executeQueryStep(AbstractQueryStep step) {
        LOGGER.debug("Executing query step {}", step);

        QueryStepResultBuilder stepResultBuilder = new QueryStepResultBuilder().setQueryStep(step);
        long startTime = System.currentTimeMillis();

        try {
            EngineQueryResult queryResult;

            if (step instanceof MonolingualQueryStep) {
                queryResult = executeMonolingualQueryStep((MonolingualQueryStep) step);
            } else if (step instanceof BilingualQueryStep) {
                queryResult = executeBilingualQueryStep((BilingualQueryStep) step);
            } else {
                throw new UnsupportedOperationException("Unsupported query step: " + step.getClass().getCanonicalName());
            }

            if (queryResult == null) {
                LOGGER.error("Query step {} failed: query result was null", step);
                stepResultBuilder.setFailedStep(true).setErrorMessage("query result was null")
                        .setExecutionTime(System.currentTimeMillis() - startTime);
                queryResult = ImmutableBilingualQueryResult.EMPTY_QUERY_RESULT;
            }
            stepResultBuilder.setEngineQueryResult(queryResult);

            long executionTime = System.currentTimeMillis() - startTime;
            stepResultBuilder.setExecutionTime(executionTime);

            LOGGER.debug("Executed query step {} in {} ms", step, executionTime);

        } catch (Exception e) {
            LOGGER.error("Query step {} failed", step, e);
            stepResultBuilder.setFailedStep(true).setErrorMessage(e.getMessage())
                    .setEngineQueryResult(ImmutableBilingualQueryResult.EMPTY_QUERY_RESULT)
                    .setExecutionTime(System.currentTimeMillis() - startTime);
        }
        return stepResultBuilder.build();
    }

    @NotNull
    private MonolingualQueryResult executeMonolingualQueryStep(@NotNull MonolingualQueryStep step) throws Exception {
        String queryString = step.getQueryString();
        Language requestLanguage = step.getRequestLanguage();

        return step.getSearchEngine().executeMonolingualQuery(queryString, requestLanguage);
    }

    @NotNull
    private BilingualQueryResult executeBilingualQueryStep(@NotNull BilingualQueryStep step) throws Exception {
        String queryString = step.getQueryString();
        Language inputLanguage = step.getInputLanguage();
        Language outLanguage = step.getOutputLanguage();
        boolean allowBothWay = step.isAllowBothWay();

        return step.getSearchEngine().executeBilingualQuery(queryString, inputLanguage, outLanguage, allowBothWay);
    }
}
