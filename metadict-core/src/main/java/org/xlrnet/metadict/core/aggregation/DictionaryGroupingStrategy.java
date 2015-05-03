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

package org.xlrnet.metadict.core.aggregation;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.api.language.BilingualDictionary;
import org.xlrnet.metadict.api.language.Language;
import org.xlrnet.metadict.api.query.BilingualEntry;
import org.xlrnet.metadict.api.query.BilingualQueryResult;
import org.xlrnet.metadict.core.query.BilingualQueryStep;
import org.xlrnet.metadict.core.query.QueryStepResult;
import org.xlrnet.metadict.core.util.FormatUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Grouping strategy that puts each requested dictionary in an own group. The target groups will be determined by the
 * language configuration of the {@link BilingualQueryStep} objects.
 * E.g. if the query planner built two steps with the configuration bidirectional de-en they will be put into the same
 * group.
 */
public class DictionaryGroupingStrategy implements GroupingStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(DictionaryGroupingStrategy.class);

    /**
     * Group the given step results with the internal strategy and return a collection of {@link ResultGroup} objects.
     *
     * @param queryStepResults
     *         An iterable of the query steps results.
     * @return a collection of groups.
     */
    @NotNull
    @Override
    public Collection<ResultGroup> groupResultSets(@NotNull Iterable<QueryStepResult> queryStepResults) {
        Map<BilingualDictionary, ResultGroupBuilder> groupBuilderMap = new HashMap<>();

        for (QueryStepResult stepResult : queryStepResults) {

            if (!(stepResult.getEngineQueryResult() instanceof BilingualQueryResult)) {
                LOGGER.debug("Skipping query result of type {}", stepResult.getEngineQueryResult().getClass().getCanonicalName());
                continue;
            }

            String searchEngineName = stepResult.getQueryStep().getSearchEngineName();

            BilingualDictionary dictionary = resolveDictionaryFromQueryStep((BilingualQueryStep) stepResult.getQueryStep());
            if (groupBuilderMap.get(dictionary) == null)
                groupBuilderMap.put(dictionary, new ResultGroupBuilder());

            ResultGroupBuilder groupBuilder = groupBuilderMap.get(dictionary);

            BilingualQueryResult engineQueryResult = (BilingualQueryResult) stepResult.getEngineQueryResult();
            for (BilingualEntry entry : engineQueryResult.getBilingualEntries()) {
                groupBuilder.addResultEntry(ResultEntryImpl.from(entry, searchEngineName));
            }
        }

        Collection<ResultGroup> resultGroups = new ArrayList<>();

        groupBuilderMap.forEach((dictionary, resultGroupBuilder) -> {
                    resultGroupBuilder.setGroupIdentifier(FormatUtils.formatDictionaryName(dictionary));
                    resultGroups.add(resultGroupBuilder.build());
                }
        );

        return resultGroups;
    }

    @NotNull
    private BilingualDictionary resolveDictionaryFromQueryStep(BilingualQueryStep stepResult) {
        Language inputLanguage = stepResult.getInputLanguage();
        Language outputLanguage = stepResult.getOutputLanguage();
        boolean bidirectional = stepResult.isAllowBothWay();

        return BilingualDictionary.fromLanguages(inputLanguage, outputLanguage, bidirectional);
    }
}
