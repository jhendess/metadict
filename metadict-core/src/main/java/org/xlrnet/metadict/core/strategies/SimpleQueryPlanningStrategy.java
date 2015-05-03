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

import org.jetbrains.annotations.NotNull;
import org.xlrnet.metadict.api.engine.SearchEngine;
import org.xlrnet.metadict.api.language.BilingualDictionary;
import org.xlrnet.metadict.core.main.EngineRegistry;
import org.xlrnet.metadict.core.query.*;

import javax.enterprise.inject.Default;

/**
 * Simple planning strategy for creating query plans. This strategy creates unoptimized query plans by just passing all
 * requested {@link BilingualDictionary} to the registry. Using this strategy should be
 * avoided, since it might cause a lot overhead while querying.
 */
@Default
public class SimpleQueryPlanningStrategy implements QueryPlanningStrategy {

    /**
     * Calculate a query plan for the given {@link QueryRequest}. The provided {@link EngineRegistry} should be used
     * for accessing the available implementations of {@link SearchEngine}.
     *
     * @param queryRequest
     *         The query request for which a query plan has to be calculated.
     * @param engineRegistry
     *         The registry where all available engines are registered.
     * @return an executable {@link QueryPlan}.
     */
    @NotNull
    @Override
    public QueryPlan calculateQueryPlan(@NotNull QueryRequest queryRequest, @NotNull EngineRegistry engineRegistry) {
        QueryPlan queryPlan = new QueryPlan();

        queryRequest.getBilingualDictionaries().forEach((d) ->
                engineRegistry.getSearchEngineNamesByDictionary(d).forEach(
                        (s) -> queryPlan.addQueryStep(
                                new BilingualQueryStep()
                                        .setInputLanguage(d.getInput())
                                        .setAllowBothWay(d.isBidirectional())
                                        .setOutputLanguage(d.getOutput())
                                        .setQueryString(queryRequest.getQueryString())
                                        .setSearchEngineName(s)
                                        .setSearchEngine(engineRegistry.getEngineByName(s))
                        )));

        queryRequest.getMonolingualLanguages()
                .forEach((l) -> engineRegistry.getSearchEngineNamesByLanguage(l)
                        .forEach((s) -> queryPlan.addQueryStep(
                                new MonolingualQueryStep()
                                        .setRequestLanguage(l)
                                        .setQueryString(queryRequest.getQueryString())
                                        .setSearchEngineName(s)
                                        .setSearchEngine(engineRegistry.getEngineByName(s))
                        )));

        return queryPlan;
    }
}
