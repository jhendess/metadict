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

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.api.language.BilingualDictionary;
import org.xlrnet.metadict.api.query.DictionaryObject;
import org.xlrnet.metadict.api.query.ExternalContent;
import org.xlrnet.metadict.api.query.MonolingualEntry;
import org.xlrnet.metadict.api.query.SynonymEntry;
import org.xlrnet.metadict.core.aggregation.GroupingType;
import org.xlrnet.metadict.core.aggregation.OrderType;
import org.xlrnet.metadict.core.aggregation.ResultGroup;
import org.xlrnet.metadict.core.main.EngineRegistryService;
import org.xlrnet.metadict.core.strategies.DefaultExecutionStrategy;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The {@link QueryService} is used for managing and coordinating the execution of {@link QueryRequest} objects.
 */
public class QueryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryService.class);

    /**
     * The registry which contains all available engines.
     */
    private final EngineRegistryService engineRegistryService;

    /**
     * The default strategy which should be used for planning queries.
     */
    private final QueryPlanningStrategy queryPlanningStrategy;

    /**
     * The strategy which should be used for executing a query plan.
     */
    private final QueryPlanExecutionStrategy queryPlanExecutionStrategy;

    @Inject
    public QueryService(EngineRegistryService engineRegistryService, QueryPlanningStrategy queryPlanningStrategy, @DefaultExecutionStrategy QueryPlanExecutionStrategy queryPlanExecutionStrategy) {
        this.engineRegistryService = engineRegistryService;
        this.queryPlanningStrategy = queryPlanningStrategy;
        this.queryPlanExecutionStrategy = queryPlanExecutionStrategy;
    }

    /**
     * Returns a new instance of {@link QueryRequestBuilder} to build a new query.
     *
     * @return a new instance of {@link QueryRequestBuilder} to build a new query.
     */
    public QueryRequestBuilder createNewQueryRequestBuilder() {
        return new QueryRequestBuilder();
    }

    /**
     * Execute a given query request and return the collected result data.
     *
     * @param queryRequest
     *         The request to execute.
     * @return The resulting data.
     */
    @NotNull
    public QueryResponse executeQuery(@NotNull QueryRequest queryRequest) {
        try {
            LOGGER.info("Incoming query request {}", queryRequest);
            return internalExecuteQuery(queryRequest);
        } catch (Exception e) {
            LOGGER.error("Query execution for query request {} failed: {}", queryRequest, e);
            // TODO: Return QueryResponse with error
            throw new RuntimeException(e);
        }
    }

    @NotNull
    protected Iterable<QueryStepResult> executeQueryPlan(@NotNull QueryPlan queryPlan) {
        LOGGER.debug("Executing query plan {} using executor {} ...", queryPlan, this.queryPlanExecutionStrategy.getClass().getSimpleName());
        Iterable<QueryStepResult> queryStepResults = this.queryPlanExecutionStrategy.executeQueryPlan(queryPlan);
        LOGGER.debug("Executed query plan {} using executor {}.", queryPlan, this.queryPlanExecutionStrategy.getClass().getSimpleName());
        return queryStepResults;
    }

    @NotNull
    protected QueryPlan prepareQueryPlan(@NotNull QueryRequest queryRequest) {
        LOGGER.debug("Calculating query plan using {} for request {} ...", this.queryPlanningStrategy.getClass().getSimpleName(), queryRequest);
        QueryPlan queryPlan = this.queryPlanningStrategy.calculateQueryPlan(queryRequest, this.engineRegistryService);
        LOGGER.debug("Calculated query plan using {} for request {}: {}", this.queryPlanningStrategy.getClass().getSimpleName(), queryRequest, queryPlan);
        return queryPlan;
    }

    protected void validateQueryRequest(@NotNull QueryRequest queryRequest) {
        checkNotNull(queryRequest, "Query request may not be null");
        checkNotNull(queryRequest.getQueryString(), "Request string may not be null");
        checkNotNull(queryRequest.getBilingualDictionaries(), "Query dictionary list may not be null");

        for (BilingualDictionary dictionary : queryRequest.getBilingualDictionaries())
            checkNotNull(dictionary, "Query dictionary in query may not be null");
    }

    @NotNull
    protected Collection<ResultGroup> groupQueryResults(@NotNull QueryRequest queryRequest, @NotNull Iterable<QueryStepResult> engineQueryResults) {
        GroupingType groupingType = queryRequest.getQueryGrouping();

        LOGGER.debug("Grouping results for query {} using strategy {} ...", queryRequest, groupingType.getGroupingStrategy().getClass().getSimpleName());
        Collection<ResultGroup> resultGroups = groupingType.getGroupingStrategy().groupResultSets(engineQueryResults);
        LOGGER.debug("Finished grouping results for query {} using strategy {}.", queryRequest, groupingType.getGroupingStrategy().getClass().getSimpleName());

        return resultGroups;
    }

    @NotNull
    private QueryResponse buildQueryResponse(@NotNull QueryRequest queryRequest, @NotNull Collection<ResultGroup> resultGroups, @NotNull List<DictionaryObject> similarRecommendations, @NotNull List<ExternalContent> externalContents, @NotNull QueryPerformanceStatistics performanceStatistics, @NotNull List<MonolingualEntry> monolingualEntries, @NotNull List<SynonymEntry> synonymEntries) {
        return new QueryResponseBuilder()
                .setQueryRequestString(queryRequest.getQueryString())
                .setQueryPerformanceStatistics(performanceStatistics)
                .setGroupedBilingualResults(resultGroups)
                .setGroupingType(queryRequest.getQueryGrouping())
                .setSimilarRecommendations(similarRecommendations)
                .setExternalContents(externalContents)
                .setMonolingualEntries(monolingualEntries)
                .setSynonymEntries(synonymEntries)
                .build();
    }

    @NotNull
    protected List<ExternalContent> collectExternalContent(@NotNull Iterable<QueryStepResult> engineQueryResults) {
        return QueryUtil.collectExternalContent(engineQueryResults);
    }

    @NotNull
    protected List<DictionaryObject> collectSimilarRecommendations(@NotNull Iterable<QueryStepResult> engineQueryResults) {
        return QueryUtil.collectSimilarRecommendations(engineQueryResults);
    }

    @NotNull
    private QueryResponse internalExecuteQuery(@NotNull QueryRequest queryRequest) {
        QueryPerformanceStatistics performanceStatistics = new QueryPerformanceStatistics();
        validateQueryRequest(queryRequest);

        long startPlanningTime = System.currentTimeMillis();
        QueryPlan queryPlan = prepareQueryPlan(queryRequest);
        // TODO: validate query plan

        long startQueryTime = System.currentTimeMillis();
        Iterable<QueryStepResult> engineQueryResults = executeQueryPlan(queryPlan);

        long startGroupingTime = System.currentTimeMillis();
        Collection<ResultGroup> resultGroups = groupQueryResults(queryRequest, engineQueryResults);

        long startOrderTime = System.currentTimeMillis();
        Collection<ResultGroup> orderedResultGroups = orderQueryResults(queryRequest, resultGroups);

        long startCollectingTime = System.currentTimeMillis();
        List<DictionaryObject> similarRecommendations = collectSimilarRecommendations(engineQueryResults);
        List<ExternalContent> externalContents = collectExternalContent(engineQueryResults);
        List<MonolingualEntry> monolingualEntries = collectMonolingualEntries(engineQueryResults);
        List<SynonymEntry> synonymEntries = collectSynonymEntries(engineQueryResults);

        long finishTime = System.currentTimeMillis();
        performanceStatistics.setPlanningPhaseDuration(startQueryTime - startPlanningTime)
                .setQueryPhaseDuration(startGroupingTime - startQueryTime)
                .setGroupPhaseDuration(startOrderTime - startGroupingTime)
                .setOrderPhaseDuration(startCollectingTime - startOrderTime)
                .setCollectPhaseDuration(finishTime - startCollectingTime)
                .setTotalDuration(finishTime - startPlanningTime);

        return buildQueryResponse(queryRequest, orderedResultGroups, similarRecommendations, externalContents, performanceStatistics, monolingualEntries, synonymEntries);
    }

    @NotNull
    private List<MonolingualEntry> collectMonolingualEntries(@NotNull Iterable<QueryStepResult> engineQueryResults) {
        return QueryUtil.collectMonolingualEntries(engineQueryResults);
    }

    @NotNull
    private List<SynonymEntry> collectSynonymEntries(@NotNull Iterable<QueryStepResult> engineQueryResults) {
        return QueryUtil.collectSynonymEntries(engineQueryResults);
    }

    @NotNull
    private Collection<ResultGroup> orderQueryResults(@NotNull QueryRequest queryRequest, @NotNull Collection<ResultGroup> resultGroups) {
        OrderType orderType = queryRequest.getQueryOrdering();

        LOGGER.debug("Sorting results for query {} using strategy {} ...", queryRequest, orderType.getOrderStrategy().getClass().getSimpleName());
        Collection<ResultGroup> sortedResultGroups = orderType.getOrderStrategy().sortResultGroups(queryRequest, resultGroups);
        LOGGER.debug("Finished sorting results for query {} using strategy {}.", queryRequest, orderType.getOrderStrategy().getClass().getSimpleName());

        return sortedResultGroups;
    }
}
