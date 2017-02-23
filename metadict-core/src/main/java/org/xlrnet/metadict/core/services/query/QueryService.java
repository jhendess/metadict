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

import com.codahale.metrics.MetricRegistry;
import com.google.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.api.language.BilingualDictionary;
import org.xlrnet.metadict.api.query.*;
import org.xlrnet.metadict.core.api.aggregation.Group;
import org.xlrnet.metadict.core.api.aggregation.ResultEntry;
import org.xlrnet.metadict.core.api.query.*;
import org.xlrnet.metadict.core.services.aggregation.group.GroupBuilder;
import org.xlrnet.metadict.core.services.aggregation.group.GroupingType;
import org.xlrnet.metadict.core.services.aggregation.merge.SimilarElementsMergeService;
import org.xlrnet.metadict.core.services.aggregation.order.OrderType;
import org.xlrnet.metadict.core.services.foundation.MetricsService;

import javax.annotation.PostConstruct;
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

    /** Service for merging similar elements inside a collection. */
    private final SimilarElementsMergeService mergeService;

    /**
     * Service for collecting metrics.
     */
    private final MetricsService metricsService;

    private MetricRegistry phaseRegistry;

    @Inject
    public QueryService(EngineRegistryService engineRegistryService, QueryPlanningStrategy queryPlanningStrategy, QueryPlanExecutionStrategy queryPlanExecutionStrategy, SimilarElementsMergeService mergeService, MetricsService metricsService) {
        this.engineRegistryService = engineRegistryService;
        this.queryPlanningStrategy = queryPlanningStrategy;
        this.queryPlanExecutionStrategy = queryPlanExecutionStrategy;
        this.mergeService = mergeService;
        this.metricsService = metricsService;
    }

    @PostConstruct
    private void initialize() {
        phaseRegistry = metricsService.getRegistryByName(QueryService.class, "phase");
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
    private QueryResponse buildQueryResponse(@NotNull QueryRequest queryRequest, @NotNull Collection<Group<ResultEntry>> resultGroups, @NotNull Collection<DictionaryObject> similarRecommendations, @NotNull Collection<ExternalContent> externalContents, @NotNull QueryPerformanceStatistics performanceStatistics, @NotNull Collection<MonolingualEntry> monolingualEntries, @NotNull Collection<SynonymEntry> synonymEntries) {
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

        long startPlanningTime = System.nanoTime();
        QueryPlan queryPlan = prepareQueryPlan(queryRequest);
        // TODO: validate query plan

        long startQueryTime = System.nanoTime();
        Iterable<QueryStepResult> engineQueryResults = executeQueryPlan(queryPlan);

        long startCollectingTime = System.nanoTime();
        Collection<DictionaryObject> similarRecommendations = collectSimilarRecommendations(engineQueryResults);
        Collection<ExternalContent> externalContents = collectExternalContent(engineQueryResults);
        Collection<MonolingualEntry> monolingualEntries = collectMonolingualEntries(engineQueryResults);
        Collection<SynonymEntry> synonymEntries = collectSynonymEntries(engineQueryResults);

        long startGroupingTime = System.nanoTime();
        Collection<Group<BilingualEntry>> bilingualGroups = groupQueryResults(queryRequest, engineQueryResults);

        long startMergingTime = System.nanoTime();
        Collection<Group<BilingualEntry>> mergedBilingualEntries = mergeBilingualEntries(bilingualGroups);
        monolingualEntries = mergeService.mergeElements(monolingualEntries, MonolingualEntry.class);
        similarRecommendations = mergeService.mergeElements(similarRecommendations, DictionaryObject.class);

        long startOrderTime = System.nanoTime();
        Collection<Group<ResultEntry>> orderedResultGroups = orderBilingualEntries(queryRequest, mergedBilingualEntries);

        long finishTime = System.nanoTime();
        performanceStatistics.setPlanningPhaseDuration(startPlanningTime - startQueryTime)
                .setQueryPhaseDuration(startCollectingTime - startQueryTime)
                .setCollectPhaseDuration(startGroupingTime - startCollectingTime)
                .setGroupPhaseDuration(startMergingTime - startGroupingTime)
                .setMergePhaseDuration(startOrderTime - startMergingTime)
                .setOrderPhaseDuration(finishTime - startOrderTime)
                .setTotalDuration(finishTime - startPlanningTime);

        recordPhaseMetrics(performanceStatistics);

        return buildQueryResponse(queryRequest, orderedResultGroups, similarRecommendations, externalContents, performanceStatistics, monolingualEntries, synonymEntries);
    }

    private void recordPhaseMetrics(QueryPerformanceStatistics performanceStatistics) {
        phaseRegistry.histogram("planning").update(performanceStatistics.getPlanningPhaseDuration());
        phaseRegistry.histogram("query").update(performanceStatistics.getQueryPhaseDuration());
        phaseRegistry.histogram("collect").update(performanceStatistics.getCollectPhaseDuration());
        phaseRegistry.histogram("group").update(performanceStatistics.getGroupPhaseDuration());
        phaseRegistry.histogram("merge").update(performanceStatistics.getMergePhaseDuration());
        phaseRegistry.histogram("order").update(performanceStatistics.getOrderPhaseDuration());
        phaseRegistry.histogram("total").update(performanceStatistics.getTotalDuration());
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
    private Collection<Group<ResultEntry>> orderBilingualEntries(@NotNull QueryRequest queryRequest, @NotNull Collection<Group<BilingualEntry>> groupsToOrder) {
        OrderType orderType = queryRequest.getQueryOrdering();

        LOGGER.trace("Sorting results for query {} using strategy {} ...", queryRequest, orderType.getOrderStrategy().getClass().getSimpleName());
        Collection<Group<ResultEntry>> sortedResultGroups = orderType.getOrderStrategy().sortResultGroups(queryRequest, groupsToOrder);
        LOGGER.trace("Finished sorting results for query {} using strategy {}.", queryRequest, orderType.getOrderStrategy().getClass().getSimpleName());

        return sortedResultGroups;
    }
}
