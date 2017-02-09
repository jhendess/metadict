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

import com.google.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.api.language.BilingualDictionary;
import org.xlrnet.metadict.api.query.*;
import org.xlrnet.metadict.core.api.aggregation.Group;
import org.xlrnet.metadict.core.api.aggregation.MergeStrategy;
import org.xlrnet.metadict.core.api.aggregation.ResultEntry;
import org.xlrnet.metadict.core.api.query.*;
import org.xlrnet.metadict.core.services.aggregation.group.GroupBuilder;
import org.xlrnet.metadict.core.services.aggregation.group.GroupingType;
import org.xlrnet.metadict.core.services.aggregation.merge.SimilarElementsMergeService;
import org.xlrnet.metadict.core.services.aggregation.order.OrderType;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The {@link QueryService} is used for managing and coordinating the execution of {@link QueryRequest} objects.
 */
@Singleton
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

    /**
     * The strategy which should be used for merging similar objects in the result set.
     */
    private final MergeStrategy mergeStrategy;

    /** Service for merging similar elements inside a collection. */
    private final SimilarElementsMergeService mergeService;

    @Inject
    public QueryService(EngineRegistryService engineRegistryService, QueryPlanningStrategy queryPlanningStrategy, QueryPlanExecutionStrategy queryPlanExecutionStrategy, MergeStrategy mergeStrategy, SimilarElementsMergeService mergeService) {
        this.engineRegistryService = engineRegistryService;
        this.queryPlanningStrategy = queryPlanningStrategy;
        this.queryPlanExecutionStrategy = queryPlanExecutionStrategy;
        this.mergeStrategy = mergeStrategy;
        this.mergeService = mergeService;
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
        LOGGER.info("Incoming query request {}", queryRequest);
        return internalExecuteQuery(queryRequest);
    }

    @NotNull
    private Iterable<QueryStepResult> executeQueryPlan(@NotNull QueryPlan queryPlan) {
        LOGGER.trace("Executing query plan {} using executor {} ...", queryPlan, this.queryPlanExecutionStrategy.getClass().getSimpleName());
        Iterable<QueryStepResult> queryStepResults = this.queryPlanExecutionStrategy.executeQueryPlan(queryPlan);
        LOGGER.trace("Executed query plan {} using executor {}.", queryPlan, this.queryPlanExecutionStrategy.getClass().getSimpleName());
        return queryStepResults;
    }

    @NotNull
    private QueryPlan prepareQueryPlan(@NotNull QueryRequest queryRequest) {
        LOGGER.trace("Calculating query plan using {} for request {} ...", this.queryPlanningStrategy.getClass().getSimpleName(), queryRequest);
        QueryPlan queryPlan = this.queryPlanningStrategy.calculateQueryPlan(queryRequest, this.engineRegistryService);
        LOGGER.trace("Calculated query plan using {} for request {}: {}", this.queryPlanningStrategy.getClass().getSimpleName(), queryRequest, queryPlan);
        return queryPlan;
    }

    private void validateQueryRequest(@NotNull QueryRequest queryRequest) {
        checkNotNull(queryRequest, "Query request may not be null");
        checkNotNull(queryRequest.getQueryString(), "Request string may not be null");
        checkNotNull(queryRequest.getBilingualDictionaries(), "Query dictionary list may not be null");

        for (BilingualDictionary dictionary : queryRequest.getBilingualDictionaries()) {
            checkNotNull(dictionary, "Query dictionary in query may not be null");
        }
    }

    @NotNull
    private Collection<Group<BilingualEntry>> groupQueryResults(@NotNull QueryRequest queryRequest, @NotNull Iterable<QueryStepResult> engineQueryResults) {
        GroupingType groupingType = queryRequest.getQueryGrouping();

        LOGGER.trace("Grouping results for query {} using strategy {} ...", queryRequest, groupingType.getGroupingStrategy().getClass().getSimpleName());
        Collection<Group<BilingualEntry>> resultGroups = groupingType.getGroupingStrategy().groupResultSets(engineQueryResults);
        LOGGER.trace("Finished grouping results for query {} using strategy {}.", queryRequest, groupingType.getGroupingStrategy().getClass().getSimpleName());

        return resultGroups;
    }

    @NotNull
    private QueryResponse buildQueryResponse(@NotNull QueryRequest queryRequest, @NotNull Collection<Group<ResultEntry>> resultGroups, @NotNull List<DictionaryObject> similarRecommendations, @NotNull List<ExternalContent> externalContents, @NotNull QueryPerformanceStatistics performanceStatistics, @NotNull List<MonolingualEntry> monolingualEntries, @NotNull List<SynonymEntry> synonymEntries) {
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
    private List<ExternalContent> collectExternalContent(@NotNull Iterable<QueryStepResult> engineQueryResults) {
        return QueryUtil.collectExternalContent(engineQueryResults);
    }

    @NotNull
    private List<DictionaryObject> collectSimilarRecommendations(@NotNull Iterable<QueryStepResult> engineQueryResults) {
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

        long startCollectingTime = System.currentTimeMillis();
        List<DictionaryObject> similarRecommendations = collectSimilarRecommendations(engineQueryResults);
        List<ExternalContent> externalContents = collectExternalContent(engineQueryResults);
        List<MonolingualEntry> monolingualEntries = collectMonolingualEntries(engineQueryResults);
        List<SynonymEntry> synonymEntries = collectSynonymEntries(engineQueryResults);

        long startGroupingTime = System.currentTimeMillis();
        Collection<Group<BilingualEntry>> bilingualGroups = groupQueryResults(queryRequest, engineQueryResults);

        long startMergingTime = System.currentTimeMillis();
        Collection<Group<BilingualEntry>> mergedBilingualEntries = mergeBilingualEntries(bilingualGroups);
        monolingualEntries = (List<MonolingualEntry>) this.mergeStrategy.mergeMonolingualEntries(monolingualEntries);   // TODO: Casting is not good, see necessary refactorings in QueryUtil

        long startOrderTime = System.currentTimeMillis();
        Collection<Group<ResultEntry>> orderedResultGroups = orderQueryResults(queryRequest, mergedBilingualEntries);

        long finishTime = System.currentTimeMillis();
        performanceStatistics.setPlanningPhaseDuration(startPlanningTime - startQueryTime)
                .setQueryPhaseDuration(startCollectingTime - startQueryTime)
                .setCollectPhaseDuration(startGroupingTime - startCollectingTime)
                .setGroupPhaseDuration(startMergingTime - startGroupingTime)
                .setMergePhaseDuration(startOrderTime - startMergingTime)
                .setOrderPhaseDuration(finishTime - startOrderTime)
                .setTotalDuration(finishTime - startPlanningTime);

        return buildQueryResponse(queryRequest, orderedResultGroups, similarRecommendations, externalContents, performanceStatistics, monolingualEntries, synonymEntries);
    }

    @NotNull
    private Collection<Group<BilingualEntry>> mergeBilingualEntries(@NotNull Collection<Group<BilingualEntry>> bilingualGroups) {
        List<Group<BilingualEntry>> mergedBilingualEntryGroups = new ArrayList<>();
        for (Group<BilingualEntry> bilingualGroup : bilingualGroups) {
            GroupBuilder<BilingualEntry> groupBuilder = new GroupBuilder<BilingualEntry>().setGroupIdentifier(bilingualGroup.getGroupIdentifier());
            groupBuilder.addAll(this.mergeService.mergeElements(bilingualGroup.getResultEntries(), BilingualEntry.class));
            mergedBilingualEntryGroups.add(groupBuilder.build());
        }

        return mergedBilingualEntryGroups;
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
    private Collection<Group<ResultEntry>> orderQueryResults(@NotNull QueryRequest queryRequest, @NotNull Collection<Group<BilingualEntry>> groupsToOrder) {
        OrderType orderType = queryRequest.getQueryOrdering();

        LOGGER.trace("Sorting results for query {} using strategy {} ...", queryRequest, orderType.getOrderStrategy().getClass().getSimpleName());
        Collection<Group<ResultEntry>> sortedResultGroups = orderType.getOrderStrategy().sortResultGroups(queryRequest, groupsToOrder);
        LOGGER.trace("Finished sorting results for query {} using strategy {}.", queryRequest, orderType.getOrderStrategy().getClass().getSimpleName());

        return sortedResultGroups;
    }
}
