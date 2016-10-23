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

package org.xlrnet.metadict.core.services.query;

import org.jetbrains.annotations.NotNull;
import org.xlrnet.metadict.api.language.BilingualDictionary;
import org.xlrnet.metadict.core.api.query.QueryPlanningStrategy;
import org.xlrnet.metadict.core.api.query.QueryRequest;

/**
 * Simple planning strategy for creating query plans. This strategy creates unoptimized query plans by just passing all
 * requested {@link BilingualDictionary} to the registry. Using this strategy should be
 * avoided, since it might cause a lot overhead while querying.
 */
public class SimpleQueryPlanningStrategy implements QueryPlanningStrategy {

    @NotNull
    @Override
    public QueryPlan calculateQueryPlan(@NotNull QueryRequest queryRequest, @NotNull EngineRegistryService engineRegistryService) {
        QueryPlan queryPlan = new QueryPlan();

        queryRequest.getBilingualDictionaries().forEach((d) ->
                engineRegistryService.getSearchEngineNamesByDictionary(d).forEach(
                        (s) -> queryPlan.addQueryStep(
                                new BilingualQueryStep()
                                        .setInputLanguage(d.getSource())
                                        .setAllowBothWay(d.isBidirectional())
                                        .setOutputLanguage(d.getTarget())
                                        .setQueryString(queryRequest.getQueryString())
                                        .setSearchEngineName(s)
                                        .setSearchEngine(engineRegistryService.getEngineByName(s))
                        )));

        queryRequest.getMonolingualLanguages()
                .forEach((l) -> engineRegistryService.getSearchEngineNamesByLanguage(l)
                        .forEach((s) -> queryPlan.addQueryStep(
                                new MonolingualQueryStep()
                                        .setRequestLanguage(l)
                                        .setQueryString(queryRequest.getQueryString())
                                        .setSearchEngineName(s)
                                        .setSearchEngine(engineRegistryService.getEngineByName(s))
                        )));

        return queryPlan;
    }
}
